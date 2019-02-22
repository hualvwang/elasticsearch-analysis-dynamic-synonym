package com.bellszhu.elasticsearch.plugin.synonym.analysis;


import com.bellszhu.elasticsearch.plugin.DynamicSynonymPlugin;
import org.elasticsearch.common.io.PathUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by liugexiang on 2019/2/22.
 */

public class ConnectionUtils {
    private static String URL = null;
    private static String USER = null;
    private static String PWD = null;
    private static String driver=null;

    static {
        String path= PathUtils
                .get(new File(DynamicSynonymPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                        .getParent(), "config")
                .toAbsolutePath().toString()+"dbconfig.properties";
        PropertiesUtils.loadFile(path);
        URL = PropertiesUtils.getPropertyValue("datasource.jdbcUrl");
        USER = PropertiesUtils.getPropertyValue("datasource.username");
        PWD = PropertiesUtils.getPropertyValue("datasource.password");
        driver=PropertiesUtils.getPropertyValue("datasource.driverClassName");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得连接的工具方法
     * @return
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PWD);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
