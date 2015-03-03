package hr;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DailyHrApiCall {
	private PreparedStatement preparedStatement = null;
	String db_url = "";
	String db_username = "";
	String db_password = "";
	String db_driver = "";
	String tG[];
	String products[];

	public DailyHrApiCall() {
		connectToDatabase();
	}

	public void updateWesDb() {
		String fToken = getFirstToken();
		String sToken = getSecondToken(fToken);
		String json = getJson(sToken);
		try {

			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonString = jsonArray.get(i).toString();
				JSONObject jsonObject = new JSONObject(jsonString);
				String employeeTeam = jsonObject.getString("Team");
				// System.out.println();
				if (employeeTeam.contains("Engineering")) {
					String employeeDesignation = jsonObject.get(
							"CurrentDesignation").toString();
					System.out.println(employeeDesignation);
					String employeeProductTeam = jsonObject
							.getString("ProductTeam");
					String employeeTG = jsonObject.getString("SubTeam");
					String employeeName = jsonObject.getString("EmplyeeName");
					String email = jsonObject.getString("Email");
					int num = getUser(email);
					updateDb(employeeName, email, employeeDesignation,
							employeeProductTeam, employeeTG, num);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// selectTG();
	}

	public void selectTG() {

		try {
			// String sql =
			// "CREATE TABLE IF NOT EXISTS SUPPORT_DAILY_DETAILS (user_email varchar(255), issue_key varchar(255),projectType varchar(255), date varchar(255),iyear varchar(255),imonth varchar(255),iweek varchar(255),iday varchar(255),timestamp varchar(255),time_spent varchar(255),attendent int);";

			Class.forName(db_driver).newInstance();

			Connection con = DriverManager.getConnection(db_url, db_username,
					db_password);
			int countTg = 0;
			Statement statement = con.createStatement();
			ResultSet count = statement
					.executeQuery("SELECT count(*) AS count FROM wes_groups");
			while (count.next()) {

				countTg = Integer.parseInt(count.getObject("count").toString());

			}
			tG = new String[countTg + 1];
			ResultSet output = statement
					.executeQuery("SELECT * FROM wes_groups");

			for (int i = 1; output.next(); i++) {

				tG[i] = output.getObject("wes_gName").toString();

			}
			int countP = 0;

			ResultSet countProject = statement
					.executeQuery("SELECT count(*) AS count FROM wes_teams");
			while (countProject.next()) {

				countP = Integer.parseInt(countProject.getObject("count")
						.toString());

			}
			products = new String[countP + 1];
			ResultSet wesProducts = statement
					.executeQuery("SELECT * FROM wes_teams");

			for (int i = 1; wesProducts.next(); i++) {

				products[i] = wesProducts.getObject("wes_tName").toString();

			}
			con.close();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public int getUser(String email) {
		int duplicate = 0;
		try {

			Class.forName(db_driver).newInstance();

			Connection con = DriverManager.getConnection(db_url, db_username,
					db_password);

			Statement statement = con.createStatement();
			ResultSet count = statement
					.executeQuery("SELECT count(*) AS count FROM wes_employees where wes_email='"
							+ email + "'");
			while (count.next()) {

				int countTg = Integer.parseInt(count.getObject("count")
						.toString());
				if (countTg == 1) {
					duplicate = 1;
				} else if (countTg == 0) {
					duplicate = 0;
				}
			}
			con.close();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return duplicate;
	}

	public void updateDb(String Name, String email, String designation,
			String productTeam, String tg, int key) {
		selectTG();
		int tid = 1;
		int gid = 1;
		String uid;
		String desig = "SE";
		String sp[] = email.split("@wso2.com");
		uid = sp[0];
		if (designation.equals(null)
				|| designation.contains("Software Engineer")) {
			desig = "SE";
		} else if (designation.contains("Senior Software Engineer")) {
			desig = "SE";
		} else if (designation.contains("Director")) {
			desig = "Dir";
		} else if (designation.contains("Associate Technical Lead")) {
			desig = "ATL";
		} else if (designation.contains("Technical Lead")) {
			desig = "TL";
		} else if (designation.contains("Senior Technical Lead")) {
			desig = "STL";
		} else if (designation.equals("Architecture")) {
			desig = "A";
		} else if (designation.contains("Senior Architecture")) {
			desig = "SA";
		}
		String spName[] = Name.split(" ");
		String fName = spName[0];
		String lName = spName[1];
		for (int i = 1; i < tG.length; i++) {
			if (tG[i].contains("/")) {
				tG[i] = tG[i].replace("/", " / ");
			}
			if (tg.contains(tG[i])) {
				tid = i;
			}
		}
		for (int i = 1; i < products.length; i++) {
			if (products[i].contains("Permanent")) {
				products[i] = products[i].replace("Permanent", "");
			} else if (products[i].contains("Rotating")) {
				products[i] = products[i].replace("Rotating", "");
			}

			if (productTeam.contains(products[i])) {
				tid = i;
			}
		}
		System.out.print("email :" + email + " wes position " + desig
				+ " fName " + fName + " lName " + lName + " gid " + gid
				+ " tid " + tid);
		try {
			Class.forName(db_driver).newInstance();

			Connection con = DriverManager.getConnection(db_url, db_username,
					db_password);
			if (key == 1) {

				String sql = "UPDATE  wes_employees SET wes_tid =?,wes_gid=?, wes_position=?  WHERE wes_email=?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, tid);
				preparedStatement.setInt(2, gid);
				preparedStatement.setString(3, desig);
				preparedStatement.setString(4, email);

				preparedStatement.executeUpdate();
				System.out.print("\nUPDATED");
			} else if (key == 0) {
				String sql = "INSERT INTO wes_employees  (wes_fName,wes_lName,wes_tid,wes_gid,wes_position,wes_email,wes_uName) values(?,?,?,?,?,?,?)";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, fName);
				preparedStatement.setString(2, lName);
				preparedStatement.setInt(3, tid);
				preparedStatement.setInt(4, gid);
				preparedStatement.setString(5, desig);
				preparedStatement.setString(6, email);
				preparedStatement.setString(7, uid);
				preparedStatement.executeUpdate();
				System.out.print("\nINSERTED");
			}

			if (!con.getAutoCommit()) {
				con.commit();

			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void connectToDatabase() {

		Properties prop = new Properties();

		try {
			// load a properties file

			prop.load(DailyHrApiCall.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			// configProp.load(in_database);
			db_url = prop.getProperty("dbUrl");
			db_username = prop.getProperty("dbUserName");
			db_password = prop.getProperty("dbPwd");
			db_driver = prop.getProperty("databaseDriver");
			Class.forName(db_driver).newInstance();

			Connection connection = DriverManager.getConnection(db_url,
					db_username, db_password);

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getFirstToken() {

		String url = "https://hr.wso2.com/hcmapi/v1/Authorization?responseType=code&clientId=xublT5nkyRqsRRh&scope=SCOPE&state=STATE&redirectUri=https://hr.wso2.com/hcmapi";
		ApiInvoke apiInvoke = new ApiInvoke();
		String fToken = apiInvoke.urlConnecter(url, "url");
		System.out.print(fToken);
		String tokenSplit[] = fToken.split("code=");
		String x[] = tokenSplit[1].split("&state=");
		fToken = x[0];
		return fToken;

	}

	public String getSecondToken(String token) {
		String url = "https://hr.wso2.com/hcmapi/v1/AccessTokens?grantType=authorization_code&code="
				+ token
				+ "&redirectUri=https://hr.wso2.com/hcmapi&clientId=xublT5nkyRqsRRh&clientSecret=TnLQPaNg6nv1eaP";
		ApiInvoke apiInvoke = new ApiInvoke();
		String sToken = apiInvoke.urlConnecter(url, "url");
		String tokenSplit[] = sToken.split("access_token=");
		String x[] = tokenSplit[1].split(" returned");
		sToken = x[0];

		return sToken;
	}

	public String getJson(String token) {
		String url = "https://hr.wso2.com/hcmapi/v1/Employees?accessToken="
				+ token;
		ApiInvoke apiInvoke = new ApiInvoke();
		String json = apiInvoke.urlConnecter(url, "json");

		return json;
	}

}
