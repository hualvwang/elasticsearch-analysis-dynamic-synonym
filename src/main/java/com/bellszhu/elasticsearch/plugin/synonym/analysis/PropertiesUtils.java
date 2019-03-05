package com.bellszhu.elasticsearch.plugin.synonym.analysis;

import com.bellszhu.elasticsearch.plugin.DynamicSynonymPlugin;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by liugexiang on 2019/2/22.
 */
public class PropertiesUtils {
    private static Logger logger = ESLoggerFactory.getLogger("dynamic-synonym");
    private static final Object o = new Object();
    //产生一个操作配置文件的对象
    private static Properties prop;

    /**
     */
    public static void loadFile(Environment env){
        if (prop == null){
            synchronized (o){
                if(prop == null){
                    prop = new Properties();
                    try {
                        File path = env.configFile().resolve(DynamicSynonymPlugin.PLUGIN_NAME).resolve("config/dbconfig.properties").toFile();
                        logger.info("load config from "+ path.toString());
                        prop.load(new FileInputStream(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    /**
     * 根据KEY取回相应的value
     * @param key
     * @return
     */
    public static String getPropertyValue(String key){
        return prop.getProperty(key);
    }

}
