package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

	private String db_url = "";
	private String db_username = "";
	private String db_password = "";
	private String db_driver = "";

	private PreparedStatement preparedStatement;

	public void connectToDatabase() {

		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(Database.class.getClassLoader().getResourceAsStream(
					"config.properties"));

			db_url = prop.getProperty("dbUrl");
			db_username = prop.getProperty("dbUserName");
			db_password = prop.getProperty("dbPwd");
			db_driver = prop.getProperty("databaseDriver");
			Class.forName(db_driver).newInstance();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteTable() {
		String sql = "DELETE FROM SONAR_COVARAGE;";

		// publisher.dataPublish(userIssues);

		try {
			Class.forName(db_driver).newInstance();

			Connection con = DriverManager.getConnection(db_url, db_username,
					db_password);

			preparedStatement = con.prepareStatement(sql);

			preparedStatement.execute();
			if (!con.getAutoCommit()) {
				con.commit();
			}
			con.close();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateDB(String branch, String codeCov) {
		String sql;
		sql = "INSERT INTO SONAR_COVARAGE (comp_name,coverage) VALUES (?,?);";

		try {
			Class.forName(db_driver).newInstance();

			Connection con = DriverManager.getConnection(db_url, db_username,
					db_password);

			preparedStatement = con.prepareStatement(sql);

			preparedStatement.setString(1, branch);

			preparedStatement.setString(2, codeCov);

			preparedStatement.executeUpdate();
			System.out
					.println(".......................DB-UPDATED...............................");
			if (!con.getAutoCommit()) {
				con.commit();
			}
			con.close();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
