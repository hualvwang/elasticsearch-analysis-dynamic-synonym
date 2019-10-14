package com.bellszhu.elasticsearch.plugin.synonym.analysis;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.*;

/**
 * Created by liugexiang on 2019/2/22.
 */
public class DBSynonymFile  implements SynonymFile {
    public static Logger logger = ESLoggerFactory.getLogger("dynamic-synonym");
    private String format;
    private boolean expand;
    private Analyzer analyzer;
    private Environment env;
    private String tableName;
    private String checkName;


    public DBSynonymFile(String format, boolean expand, Analyzer analyzer, Environment env, String tableName, String checkName) {
        this.format = format;
        this.expand = expand;
        this.analyzer = analyzer;
        this.env = env;
        this.tableName = tableName;
        this.checkName = checkName;
    }

    public DBSynonymFile(String format, boolean expand) {
        this.format = format;
        this.expand = expand;
    }

    @Override
    public SynonymMap reloadSynonymMap() {
        try {
            logger.info("从数据库加载同义词");
            Reader rulesReader = getReader();
            SynonymMap.Builder parser = null;
            if ("wordnet".equalsIgnoreCase(format)) {
                parser = new WordnetSynonymParser(true, expand, analyzer);
                ((WordnetSynonymParser) parser).parse(rulesReader);
            } else {
                parser = new SolrSynonymParser(true, expand, analyzer);
                ((SolrSynonymParser) parser).parse(rulesReader);
            }
            return parser.build();
        } catch (Exception e) {
            logger.error("从数据库加载同义词出错!", e, "db");
            throw new IllegalArgumentException(
                    "could not reload DB synonyms file to build synonyms", e);
        }
    }

    @Override
    public boolean isNeedReloadSynonymMap() {
        Connection connection = ConnectionUtils.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int status = 0;
        try {
            preparedStatement = connection.prepareStatement("select refreshStatus from " + checkName + " where type = 'tyc'");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                status = resultSet.getInt(1);
            }
            if (status == 1) {
                preparedStatement = connection.prepareStatement("update "+ checkName +" set refreshStatus=0 where type = 'tyc'" );
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return status == 1;
    }


    @Override
    public Reader getReader() {
        Reader reader = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = ConnectionUtils.getConnection();
        if(connection == null){
            logger.warn("获取数据库连接失败");
            throw new IllegalArgumentException("获取数据库连接失败");
        }
        StringBuilder str = new StringBuilder();
        try {
            preparedStatement = connection.prepareStatement("select synonym from  " +tableName+"   where status=1");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String s = resultSet.getString(1);
                str.append(s).append("\r\n");
            }
            reader = new InputStreamReader(new ByteArrayInputStream(str.toString().getBytes()), "utf-8");

        } catch (SQLException e) {
            logger.error("get DB synonym reader {} error!", e, "db");
            throw new IllegalArgumentException(
                    "DB while reading local synonyms file", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(
                    " while reading  synonyms file", e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reader;
    }
}
