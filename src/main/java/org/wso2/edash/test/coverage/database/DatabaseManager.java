package org.wso2.edash.test.coverage.database;

import org.apache.tomcat.jdbc.pool.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Manager class (singleton)
 * Will return a database connection using Mysql DataSource.
 */
public class DatabaseManager {
	private static DatabaseManager instance;
	private DataSource dataSource;

	/**
	 * private constructor
	 */
	private DatabaseManager() throws IOException {
		this.getDataSource();
	}

	/**
	 * Returns the DatabaseManager instance
	 *
	 * @return instance
	 */
	public static DatabaseManager getInstance() throws IOException {
		if (instance == null) {
			synchronized (DatabaseManager.class) {
				if (instance == null) {
					instance = new DatabaseManager();
				}
			}
		}
		return instance;
	}

	/**
	 * Sets the data source properties and returns the DataSource object.
	 *
	 * @throws java.io.IOException
	 */
	public void getDataSource() throws IOException {
		DataSource dataSource;
		Properties properties = new Properties();
		properties.load(DatabaseManager.class.getClassLoader()
		                                     .getResourceAsStream("db_config.properties"));
		dataSource = new DataSource();
		dataSource.setDriverClassName(properties.getProperty("databaseDriver"));
		dataSource.setUrl(properties.getProperty("dbURl"));
		dataSource.setUsername(properties.getProperty("dbUserName"));
		dataSource.setPassword(properties.getProperty("dbPwd"));
		this.dataSource = dataSource;
	}

	/**
	 * Returns the database connection using the MysqlDataSource object
	 *
	 * @return connection
	 * @throws java.sql.SQLException
	 */
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
