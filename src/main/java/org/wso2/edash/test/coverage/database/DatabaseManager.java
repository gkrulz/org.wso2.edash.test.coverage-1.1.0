package org.wso2.edash.test.coverage.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by yashira on 6/13/14.
 */

/***
 * Database management class
 */
public final class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    private static Object lock = new Object();

    /**
     * private constructor
     */
    private DatabaseManager(){

    }

    /**
     * Returns the DatabaseManager instance
     * @return
     */
    public static DatabaseManager getInstance(){
        synchronized (lock){
            if(instance == null){
                instance = new DatabaseManager();
            }
        }
        return instance;
    }

    /**
     * Sets the data source properties and returns the MysqlDataSource object
     * @return
     * @throws IOException
     */
    public static MysqlDataSource getMysqlDataSource() throws IOException {
        MysqlDataSource mysqlDataSource;
        Properties properties = new Properties();
        properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("test_coverage_constants.properties"));
        mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(properties.getProperty("dbUrl"));
        mysqlDataSource.setUser(properties.getProperty("dbUserName"));
        mysqlDataSource.setPassword(properties.getProperty("dbPwd"));
        return mysqlDataSource;
    }

    /**
     * Returns the database connection using the MysqlDataSource object
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DatabaseManager.getMysqlDataSource().getConnection();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return connection;
    }
}
