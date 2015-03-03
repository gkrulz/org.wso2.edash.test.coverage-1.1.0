package org.wso2.carbon.edb.summary.helper.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DBUtil {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(EDBConfig.getInstance().getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager
                .getConnection(EDBConfig.getInstance().getDatabaseURL(),
                        EDBConfig.getInstance().getDatabaseUserName(),
                        EDBConfig.getInstance().getDatabasePassword());
    }


}
