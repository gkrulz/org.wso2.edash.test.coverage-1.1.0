package schedule;

import invoke.SupportJiraRestAPIInvoke;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import publish.PrintLog;

public class SupportJiraRestAPICall {
	public PrintLog log = new PrintLog();
	private Properties prop = new Properties();
	private SupportJiraRestAPIInvoke issuesRestAPI;
	private Connection conn;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String db_url = "";
	private String db_username = "";
	private String db_password = "";
	private String db_driver = "";
	private int userCount;
	private Statement statement;
	private String[] userArray;

	public SupportJiraRestAPICall() {
		getProperties();
		connectToDatabase();
		userCount = totalUsers();
		userArray = new String[totalUsers() + 1];
		getUsers();
	}

	public int totalUsers() {
		int totalUserCount = 0;
		try {
			Connection conn = DriverManager.getConnection(db_url, db_username,
					db_password);
			preparedStatement = conn
					.prepareStatement("SELECT * FROM jiraUsers ORDER BY id DESC LIMIT 1");

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				totalUserCount = Integer.parseInt(resultSet.getString("id"));
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return totalUserCount;
	}

	public void getUsers() {
		try {
			Class.forName(db_driver).newInstance();
			Connection conn = DriverManager.getConnection(db_url, db_username,
					db_password);

			statement = conn.createStatement();
			ResultSet resultSet = statement
					.executeQuery("SELECT name FROM jiraUsers");
			for (int i = 1; resultSet.next(); ++i) {
				userArray[i] = resultSet.getString("name");
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getUserIssuesDetails(String current, String next, String uname,
			String unumber) {
		Object[] url_arguments = { current, next, uname };
		String createdIssueUrl = prop.getProperty("SUPPORT_JIRA_URL");

		String createdIssue_userUrl = MessageFormat.format(createdIssueUrl,
				url_arguments);

		issuesRestAPI = new SupportJiraRestAPIInvoke();
		String issues = issuesRestAPI.invoke(createdIssue_userUrl);

		String format = "yyyy-MM-dd";

		SimpleDateFormat dateformat = new SimpleDateFormat(format);

		int week = 0;
		try {
			System.out.println("\n   date " + current);
			log.write("\n   date " + current);
			JSONArray allIssues = null;
			JSONObject issuesJson = null;

			allIssues = filterRestApi(issues, "issues");

			for (int i = 0; i < allIssues.length(); ++i) {
				issuesJson = allIssues.getJSONObject(i);
				String issuesLinkList = issuesJson.get("self").toString();
				String issueKey = issuesJson.get("key").toString();

				String issueOutput = issuesRestAPI.invoke(issuesLinkList);

				issuesJson = new JSONObject(issueOutput);

				JSONArray workLogArray = issuesJson.getJSONObject("fields")
						.getJSONObject("worklog").getJSONArray("value");
				String projectType = issuesJson.getJSONObject("fields")
						.getJSONObject("project").getJSONObject("value")
						.get("name").toString();
				if (((!(projectType.contains("Evaluation Support")))
						&& (!(projectType.contains("Production Support"))) && (!(projectType
							.contains("Development Support"))))
						|| (projectType.contains("Internal"))
						|| (projectType.contains("WSO2")))
					continue;
				System.out.println("Project type " + projectType + "/n");
				log.write("Project type " + projectType + "\n");
				for (int j = 0; j < workLogArray.length(); ++j) {
					String assignee = workLogArray.getJSONObject(j)
							.getJSONObject("author").get("name").toString();
					String workLog = workLogArray.getJSONObject(j)
							.get("minutesSpent").toString();
					String date = workLogArray.getJSONObject(j).get("started")
							.toString();
					String dateArray[] = date.split("T");
					String issueDate = dateArray[0];
					String issueFormat = "yyyy-MM-dd";

					SimpleDateFormat issueDateFormat = new SimpleDateFormat(
							issueFormat);

					Date getIssueDate = issueDateFormat.parse(issueDate);
					Calendar cal = Calendar.getInstance();
					cal.setTime(getIssueDate);
					week = cal.get(4);
					String[] dateSplit = issueDate.split("-");
					System.out.println("\nWeek = " + week + "  Attended  date "
							+ issueDate);
					log.write("\nWeek = " + week + "    Attended date "
							+ issueDate);

					try {
						int attendent = 0;
						String sql = "CREATE TABLE IF NOT EXISTS SUPPORT_DAILY_DETAILS (user_email varchar(255), issue_key varchar(255),projectType varchar(255), date varchar(255),iyear varchar(255),imonth varchar(255),iweek varchar(255),iday varchar(255),timestamp varchar(255),time_spent varchar(255),attendent int);";
						Class.forName(db_driver).newInstance();

						Connection con = DriverManager.getConnection(db_url,
								db_username, db_password);

						preparedStatement = con.prepareStatement(sql);
						preparedStatement.executeUpdate();

						Statement statement = con.createStatement();
						ResultSet output = statement
								.executeQuery("SELECT COUNT(*) AS ISSUE_COUNT FROM SUPPORT_DAILY_DETAILS WHERE issue_key ='"
										+ issueKey
										+ "' AND user_email ='"
										+ assignee
										+ "' AND iyear ='"
										+ dateSplit[0]
										+ "' and imonth = '"
										+ dateSplit[1]
										+ "' and iweek = '"
										+ week + "'");
						while (output.next()) {
							if (output.getInt("ISSUE_COUNT") != 0) {
								attendent = 0;
							} else {
								attendent = 1;
							}

						}

						ResultSet outputHours = statement
								.executeQuery("SELECT COUNT(*) AS HOUR_COUNT FROM SUPPORT_DAILY_DETAILS WHERE issue_key ='"
										+ issueKey
										+ "' AND user_email ='"
										+ assignee
										+ "' AND timestamp ='"
										+ date + "'");
						while (outputHours.next()) {
							if (outputHours.getInt("HOUR_COUNT") == 0)
								continue;
							workLog = "0";
						}

						sql = " INSERT INTO SUPPORT_DAILY_DETAILS (user_email,issue_key,projectType,date,iyear,imonth,iweek,iday,timestamp,time_spent,attendent) SELECT * FROM (SELECT ?,?,?,?,?,?,?,?,?,?,?) AS tmp WHERE NOT EXISTS (SELECT user_email,issue_key,date,iyear,imonth,iweek,iday,timestamp FROM SUPPORT_DAILY_DETAILS WHERE user_email = ? AND issue_key = ? AND date = ? AND iyear = ? AND imonth = ? AND iweek = ? AND iday = ? AND timestamp = ?) LIMIT 1;";

						preparedStatement = con.prepareStatement(sql);
						preparedStatement.setString(1, assignee);
						preparedStatement.setString(2, issueKey);
						preparedStatement.setString(3, projectType);
						preparedStatement.setString(4, issueDate);
						preparedStatement.setString(5, dateSplit[0]);
						preparedStatement.setString(6, dateSplit[1]);
						preparedStatement.setString(7, Integer.toString(week));
						preparedStatement.setString(8, dateSplit[2]);
						preparedStatement.setString(9, date);
						preparedStatement.setString(10, workLog);
						preparedStatement.setInt(11, attendent);

						preparedStatement.setString(12, assignee);
						preparedStatement.setString(13, issueKey);
						preparedStatement.setString(14, current);
						preparedStatement.setString(15, dateSplit[0]);
						preparedStatement.setString(16, dateSplit[1]);
						preparedStatement.setString(17, Integer.toString(week));
						preparedStatement.setString(18, dateSplit[2]);
						preparedStatement.setString(19, date);
						preparedStatement.executeUpdate();

						con.close();

						System.out.println("Issue Key " + issueKey + " User = "
								+ assignee + " work log=" + workLog
								+ " attendent = " + attendent + " Date = "
								+ current + "\n");
						log.write("Issue Key " + issueKey + " User = "
								+ assignee + " work log=" + workLog
								+ " attendent = " + attendent + " Date = "
								+ current + "\n");
						System.out
								.println("*********************************************************published & updated ********************************************************\n");

						log.write("*********************************************************published & updated ******************************************************** \n");
					} catch (SQLException localSQLException) {
					} catch (InstantiationException e) {
						Writer writer = new StringWriter();
						PrintWriter printWriter = new PrintWriter(writer);
						e.printStackTrace(printWriter);
						String s = writer.toString();
						e.printStackTrace();

					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	public void updateDatabase(String current, String next, int usernum) {
		String sql = "UPDATE wes_lastSupportJiraRestAPICall SET last_update_date =?,dayafter_last_update=?,last_updated_user=?  WHERE schedule_task=?";
		try {
			Class.forName(db_driver).newInstance();
			Connection conn = DriverManager.getConnection(db_url, db_username,
					db_password);
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, current);
			preparedStatement.setString(2, next);

			preparedStatement.setString(3, Integer.toString(usernum));

			preparedStatement.setString(4, "SupportJira");

			preparedStatement.executeUpdate();
			conn.close();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public JSONArray filterRestApi(String input, String filter) {
		JSONArray filterRestCall = null;
		try {
			JSONObject getRestJson = new JSONObject(input);
			filterRestCall = getRestJson.getJSONArray(filter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return filterRestCall;
	}

	public void connectToDatabase() {
		Properties prop = new Properties();
		try {
			prop.load(SupportJiraRestAPICall.class.getClassLoader()
					.getResourceAsStream("config.properties"));

			db_url = prop.getProperty("dbUrl");
			db_username = prop.getProperty("dbUserName");
			db_password = prop.getProperty("dbPwd");
			db_driver = prop.getProperty("databaseDriver");
			Class.forName(db_driver).newInstance();

			conn = DriverManager
					.getConnection(db_url, db_username, db_password);
			conn.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] getLastUpadatedDetails() {
		String[] lastUpdatedDetails = new String[3];
		try {
			Class.forName(db_driver);
			Connection conn = DriverManager.getConnection(db_url, db_username,
					db_password);

			preparedStatement = conn
					.prepareStatement("SELECT last_update_date,dayafter_last_update,last_updated_user FROM wes_lastSupportJiraRestAPICall WHERE schedule_task=?");
			preparedStatement.setString(1, "SupportJira");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				lastUpdatedDetails[0] = resultSet.getString("last_update_date");
				lastUpdatedDetails[1] = resultSet
						.getString("dayafter_last_update");
				lastUpdatedDetails[2] = resultSet
						.getString("last_updated_user");
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lastUpdatedDetails;
	}

	public void getProperties() {
		try {
			prop.load(SupportJiraRestAPICall.class.getClassLoader()
					.getResourceAsStream("constants.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void getIssues() {
		String[] lastUpadatedDetails = new String[3];

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
		end.add(5, -1);

		int lastUpadatedUserIndex = -1;

		for (int i = 0; i < userCount; ++i) {
			if (Integer.toString(i).equals(lastUpadatedDetails[2].toString())) {
				lastUpadatedUserIndex = i;
				break;
			}
		}

		while (!(start.after(end))) {
			if (lastUpadatedUserIndex == userCount) {
				lastUpadatedUserIndex = 1;
				start.add(5, 1);
				start_1.add(5, 1);
			} else {
				++lastUpadatedUserIndex;
			}

			Date targetDay = start.getTime();
			Date targetDay_1 = start_1.getTime();
			String startS = dateFormat.format(targetDay);
			String startS_1 = dateFormat.format(targetDay_1);

			for (int j = lastUpadatedUserIndex; j < userCount + 1; ++j) {
				startS = startS.replaceAll("/", "-");
				startS_1 = startS_1.replaceAll("/", "-");

				Object[] arguments = { startS, startS_1, userArray[j] };

				String[] userIssues = { "jira", Integer.toString(j),
						userArray[j], startS };
				System.out
						.print("**********************Support - JIRA - REST - API - CALL **************************** \n");
				log.write("\n**********************Support - JIRA - REST - API - CALL **************************** \n");
				System.out.print(j + ":" + userArray[j] + ":" + startS);
				log.write(j + ":" + userArray[j] + ":" + startS);

				getUserIssuesDetails(startS, startS_1, userArray[j],
						Integer.toString(j));
				updateDatabase(startS, startS_1, j);

				lastUpadatedUserIndex = j;
			}
		}

		System.exit(0);
	}
}
