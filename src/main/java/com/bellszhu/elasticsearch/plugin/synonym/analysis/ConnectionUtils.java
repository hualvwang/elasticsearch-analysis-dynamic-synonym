package com.bellszhu.elasticsearch.plugin.synonym.analysis;


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
        PropertiesUtils.loadFile("dbconfig.properties");
        URL = PropertiesUtils.getPropertyValue("spring.datasource.sqlserver.hlv_66law_article.jdbcUrl");
        USER = PropertiesUtils.getPropertyValue("spring.datasource.sqlserver.hlv_66law_article.username");
        PWD = PropertiesUtils.getPropertyValue("spring.datasource.sqlserver.hlv_66law_article.password");
        driver=PropertiesUtils.getPropertyValue("spring.datasource.sqlserver.hlv_66law_article.driverClassName");
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
