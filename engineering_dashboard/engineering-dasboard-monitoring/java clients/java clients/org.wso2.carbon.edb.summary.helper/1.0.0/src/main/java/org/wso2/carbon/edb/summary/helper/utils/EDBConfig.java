package org.wso2.carbon.edb.summary.helper.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EDBConfig {
    private String databaseURL;
    private String databaseUserName;
    private String databasePassword;
    private String databaseDriver;

    private static EDBConfig instance;

    private Properties configProp;
    private InputStream in;


    private EDBConfig() {
        configProp = new Properties();
        in = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "org/wso2/carbon/edb/summary/helper/resources/databaseConf.properties");

        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static EDBConfig getInstance() {
        if (null == instance) {
            instance = new EDBConfig();
        }
        return instance;
    }


    public String getDatabaseURL() {
        return configProp.getProperty("databaseURL");
    }

    public void setDatabaseURL() {
        this.databaseURL = configProp.getProperty("databaseURL");
    }

    public String getDatabaseUserName() {
        return databaseUserName;
    }

    public void setDatabaseUserName() {
        this.databaseUserName = configProp.getProperty("databaseUserName");
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = configProp.getProperty("databasePassword");
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = configProp.getProperty("databaseDriver");
    }
}
