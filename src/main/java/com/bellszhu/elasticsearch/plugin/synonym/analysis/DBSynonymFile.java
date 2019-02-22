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


    public DBSynonymFile(String format, boolean expand, Analyzer analyzer, Environment env) {
        this.format = format;
        this.expand = expand;
        this.analyzer = analyzer;
        this.env = env;
    }

    public DBSynonymFile(String format, boolean expand) {
        this.format = format;
        this.expand = expand;
    }

    @Override
    public SynonymMap reloadSynonymMap() {
        try {
            logger.info("start reload DB synonym from {}.", "db");
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
            logger.error("reload db synonym {} error!", e, "db");
            throw new IllegalArgumentException(
                    "could not reload DB synonyms file to build synonyms", e);
        }
    }

    private boolean isNeedReloadSynonymMap=false;
    @Override
    public boolean isNeedReloadSynonymMap() {
                return isNeedReloadSynonymMap;
    }



    @Override
    public Reader getReader() {
        Reader reader = null;
        Connection connection=ConnectionUtils.getConnection();
        StringBuilder str=new StringBuilder();
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("select synony from  elastic_serach_synony");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                String s = resultSet.getString(1);
                str.append(s+"\r\n");
            }
            if(str.toString().length()>0)isNeedReloadSynonymMap=true;
            reader=new InputStreamReader(new ByteArrayInputStream(str.toString().getBytes()),"utf-8");

        } catch (SQLException e) {
            logger.error("get DB synonym reader {} error!", e, "db");
            throw new IllegalArgumentException(
                    "DB while reading local synonyms file", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(
                    " while reading  synonyms file", e);
        } finally {
        }
        return reader;
    }
}
