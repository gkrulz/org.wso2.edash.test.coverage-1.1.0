package DatabaseHanddler;

import java.io.IOException;
import java.util.Properties;

public class DatabaseConnecter {

	private String dbUrl;

	public DatabaseConnecter() {
		// TODO Auto-generated constructor stub
		connectToDatabase();
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}

	public String[] getDbArry() {
		return dbArry;
	}

	public void setDbArry(String[] dbArry) {
		this.dbArry = dbArry;
	}

	private String dbUserName;
	private String dbPwd;
	private String databaseDriver;

	String[] dbArry = new String[5];

	public void connectToDatabase() {

		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(DatabaseConnecter.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			setDbUrl(prop.getProperty("dbUrl"));
			setDbUserName(prop.getProperty("dbUserName"));
			setDbPwd(prop.getProperty("dbPwd"));
			setDatabaseDriver(prop.getProperty("databaseDriver"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
