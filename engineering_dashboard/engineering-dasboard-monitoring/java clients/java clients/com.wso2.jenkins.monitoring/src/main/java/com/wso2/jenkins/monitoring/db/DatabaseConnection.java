package com.wso2.jenkins.monitoring.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Padmaka Wijayagoonawardena on 1/20/15.
 * Email - padmakaj@wso2.com
 */
public class DatabaseConnection {
    public static final Logger LOGGER = LogManager.getLogger(DatabaseConnection.class.getName());
    private Properties prop = new Properties();
    private Connection conn;
    private String db_url = "";
    private String db_username = "";
    private String db_password = "";
    private String db_driver = "";
    private PreparedStatement preparedStatement = null;

    public DatabaseConnection(){
        connectToDatabase();
        createTable();
    }

    public void connectToDatabase() {
        Properties prop = new Properties();
        try {
            prop.load(DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("config.properties"));

            db_url = prop.getProperty("dbUrl");
            db_username = prop.getProperty("dbUserName");
            db_password = prop.getProperty("dbPwd");
            db_driver = prop.getProperty("databaseDriver");
            Class.forName(db_driver).newInstance();

            conn = DriverManager
                    .getConnection(db_url, db_username, db_password);
        } catch (IOException ex) {
            LOGGER.debug(ex);
        } catch (ClassNotFoundException e) {
            LOGGER.debug(e);
        } catch (SQLException e) {
            LOGGER.debug(e);
        } catch (Exception e) {
            LOGGER.debug(e);
        }
    }

    public void createTable(){
        try{
            String sql = "CREATE TABLE IF NOT EXISTS JENKINS_DETAILS ("+
                    "job_name varchar(255) PRIMARY KEY, "+
                    "job_url varchar(255), "+
                    "job_color varchar(255), "+
                    "last_success_no int, "+
                    "last_success_url varchar(255),"+
                    "last_failure_no int,"+
                    "last_failure_url varchar(255))";
            Class.forName(db_driver).newInstance();

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();

            String deleteStatement = "DELETE FROM JENKINS_DETAILS";
            preparedStatement = conn.prepareStatement(deleteStatement);
            preparedStatement.executeUpdate();

        }catch (SQLException localSQLException) {
        } catch (InstantiationException e) {
            LOGGER.debug(e);
        } catch (IllegalAccessException e) {
            LOGGER.debug(e);
        } catch (ClassNotFoundException e) {
            LOGGER.debug(e);
        }
    }

    public void writeData(String job_name,
                          String job_url, String job_color,
                          String last_success_no, String last_success_url,
                          String last_failure_no, String last_failure_url){
        try{
            String insertStatement = "INSERT INTO JENKINS_DETAILS (job_name, "+
                    "job_url, job_color, last_success_no, "+
                    "last_success_url, last_failure_no, last_failure_url) "+
                    "VALUES(?,?,?,?,?,?,?)";
            preparedStatement = conn.prepareStatement(insertStatement);
            preparedStatement.setString(1, job_name);
            preparedStatement.setString(2, job_url);
            preparedStatement.setString(3, job_color);
            preparedStatement.setString(4, last_success_no);
            preparedStatement.setString(5, last_success_url);
            preparedStatement.setString(6, last_failure_no);
            preparedStatement.setString(7, last_failure_url);
            preparedStatement.executeUpdate();

        }catch (SQLException localSQLException) {
            LOGGER.debug(localSQLException);
        }
    }
}
