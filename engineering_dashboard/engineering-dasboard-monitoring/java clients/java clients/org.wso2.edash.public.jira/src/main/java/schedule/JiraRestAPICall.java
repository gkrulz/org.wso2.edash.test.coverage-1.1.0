package schedule;

import invoke.JiraRestAPIInvoke;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.security.sasl.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.NoStreamDefinitionExistException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import publish.IssuesPublisher;
import publish.PrintLog;

public class JiraRestAPICall {

	public PrintLog log = new PrintLog();
	private Properties prop = new Properties();
	private JiraRestAPIInvoke issuesRestAPI;
	private int userCount;
	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String db_url = "";
	private String db_username = "";
	private String db_password = "";
	private String db_driver = "";
	private String userArray[];
	public IssuesPublisher publisher;

	public JiraRestAPICall() {
		publisher = new IssuesPublisher();
		getProperties();
		connectToDatabase();
		userCount = totalUsers();
		userArray = new String[totalUsers() + 1];
		getUsers();

	}

	public void getRedMineIssues() {

		String lastUpadatedDetails[] = new String[3];

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		lastUpadatedDetails = getLastUpadatedDetails();

		String startDateString = lastUpadatedDetails[0].toString();
		String startDate_1_String = lastUpadatedDetails[1].toString();

		startDateString = startDateString.replaceAll("-", "/");
		startDate_1_String = startDate_1_String.replaceAll("-", "/");

		Date startDate = new Date(startDateString);
		Calendar start = Calendar.getInstance();

		Date startDate_1 = new Date(startDate_1_String);
		Calendar start_1 = Calendar.getInstance();

		start.setTime(startDate);
		start_1.setTime(startDate_1);

		Date currrentDate = new Date();
		Calendar end = Calendar.getInstance();
		end.setTime(currrentDate);
		end.add(Calendar.DATE, -1);

		int lastUpadatedUserIndex = -1;

		for (int i = 0; i < userCount; i++) {
			// /add users

			if (Integer.toString(i).equals(lastUpadatedDetails[2].toString())) {
				lastUpadatedUserIndex = i;
				break;
			}
		}

		while (!start.after(end)) {

			if (lastUpadatedUserIndex == userCount) {
				lastUpadatedUserIndex = 1;
				start.add(Calendar.DATE, 1);
				start_1.add(Calendar.DATE, 1);
			} else {
				lastUpadatedUserIndex = lastUpadatedUserIndex + 1;
			}

			Date targetDay = start.getTime();
			Date targetDay_1 = start_1.getTime();
			String startS = dateFormat.format(targetDay);
			String startS_1 = dateFormat.format(targetDay_1);

			for (int j = lastUpadatedUserIndex; j < userCount + 1; j++) {

				startS = startS.replaceAll("/", "-");
				startS_1 = startS_1.replaceAll("/", "-");

				Object[] arguments = { startS, startS_1, userArray[j] };

				String createdIssues = getCreatedIssuesPerUser(arguments);
				String resolvedIssues = getResolvedIssuesPerUser(arguments);

				String userIssues[] = { "jira", Integer.toString(j),
						userArray[j], startS, createdIssues, resolvedIssues };
				for (int y = 1; y < userIssues.length; y++) {
					System.out.print(userIssues[y] + ":");
					log.write(userIssues[y] + ":");
				}

				// lastUpadatedUserIndex = j;

				try {
					String sql = "UPDATE wes_lastJiraRestAPICall SET last_update_date =?,dayafter_last_update=?, last_updated_user =?  WHERE schedule_task=?";
					publisher.dataPublish(userIssues);
					Class.forName(db_driver).newInstance();

					Connection con = DriverManager.getConnection(db_url,
							db_username, db_password);

					preparedStatement = con.prepareStatement(sql);
					preparedStatement.setString(1, startS);
					preparedStatement.setString(2, startS_1);
					preparedStatement.setString(3, Integer.toString(j));
					preparedStatement.setString(4, "Jira");

					preparedStatement.executeUpdate();

					if (!con.getAutoCommit()) {
						con.commit();

					}
					con.close();
					log.write("******************************published and Updated************************************\n");
					System.out
							.println("******************************published and Updated************************************");
					lastUpadatedUserIndex = j;
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AgentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedStreamDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StreamDefinitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DifferentStreamDefinitionAlreadyDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoStreamDefinitionExistException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (org.wso2.carbon.databridge.commons.exception.AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		System.exit(0);

	}

	public void getUsers() {

		try {

			Class.forName(db_driver).newInstance();
			Connection conn = DriverManager.getConnection(db_url, db_username,
					db_password);

			statement = conn.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT name FROM jiraUsers");
			for (int i = 1; resultSet.next(); i++) {
				userArray[i] = resultSet.getString("name");

			}
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getCreatedIssuesPerUser(Object[] url_arguments) {

		String createdIssueUrl = prop.getProperty("JIRA_CREATED_ISSUES");

		String createdIssue_userUrl = MessageFormat.format(createdIssueUrl,
				url_arguments);

		issuesRestAPI = new JiraRestAPIInvoke();
		String created_issues = issuesRestAPI.invoke(createdIssue_userUrl);

		JSONObject createdIssuesJson = null;
		String userCreatedIssues = "";

		try {
			createdIssuesJson = new JSONObject(created_issues);

			userCreatedIssues = createdIssuesJson.get("total").toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userCreatedIssues;

	}

	public String getResolvedIssuesPerUser(Object[] url_arguments) {

		String resolvedIssueUrl = prop.getProperty("JIRA_RESOLVED_ISSUES");

		String resolvedIssue_userUrl = MessageFormat.format(resolvedIssueUrl,
				url_arguments);

		issuesRestAPI = new JiraRestAPIInvoke();
		String resolved_issues = issuesRestAPI.invoke(resolvedIssue_userUrl);

		JSONObject resolvedIssuesJson;
		String userResolvedIssues = "";

		try {

			resolvedIssuesJson = new JSONObject(resolved_issues);
			userResolvedIssues = resolvedIssuesJson.get("total").toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userResolvedIssues;

	}

	public void connectToDatabase() {

		Properties prop = new Properties();

		try {
			// load a properties file
			// prop.load(new FileInputStream("resources/config.properties"));
			prop.load(JiraRestAPICall.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			// configProp.load(in_database);
			db_url = prop.getProperty("dbUrl");
			db_username = prop.getProperty("dbUserName");
			db_password = prop.getProperty("dbPwd");
			db_driver = prop.getProperty("databaseDriver");
			Class.forName(db_driver).newInstance();

			connection = DriverManager.getConnection(db_url, db_username,
					db_password);

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

	public int totalUsers() {

		int totalUserCount = 0;

		try {

			preparedStatement = connection
					.prepareStatement("SELECT * FROM jiraUsers ORDER BY id DESC LIMIT 1");
			// preparedStatement.setString(1, "restAPI");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				totalUserCount = Integer.parseInt(resultSet.getString("id"));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return totalUserCount;

	}

	public String[] getLastUpadatedDetails() {

		String lastUpdatedDetails[] = new String[3];

		try {
			Connection con;
			Class.forName(db_driver);
			con = DriverManager.getConnection(db_url, db_username, db_password);

			preparedStatement = connection
					.prepareStatement("SELECT  last_update_date,dayafter_last_update,last_updated_user  FROM wes_lastJiraRestAPICall WHERE schedule_task=?");
			preparedStatement.setString(1, "Jira");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				lastUpdatedDetails[0] = resultSet.getString("last_update_date");
				lastUpdatedDetails[1] = resultSet
						.getString("dayafter_last_update");

				lastUpdatedDetails[2] = resultSet
						.getString("last_updated_user");

			}
			con.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lastUpdatedDetails;

	}

	public void getProperties() {

		try {
			// load a properties file
			// prop.load(new FileInputStream("resources/constants.properties"));
			prop.load(JiraRestAPICall.class.getClassLoader()
					.getResourceAsStream("constants.properties"));
			//

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
