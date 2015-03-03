package DailyRestInvoke;

import Connection.InvokeRest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 * Created by Padmaka Wijayagoonawardena on 10/13/14.
 * Email - padmakaj@wso2.com
 */

/**
 * class for getting all the necessary data and writing it to the database
 */
public class RestApiCall {
    public static final Logger LOGGER = LogManager.getLogger(RestApiCall.class.getName());
    private Properties properties = new Properties();
    String USER = "";
    String PASS = "";
    String JDBC_DRIVER = null;
    String DB_URL = null;
    String userDir = "";
    String sql;
    Connection conn = null;
    Statement stmt = null;

    public RestApiCall() {
        userDir = System.getProperty("user.dir");
        getProperties();
    }

    /**
     * getting the properties needed
     */
    public void getProperties() {
        try {
            Properties prop2 = new Properties();
            prop2.load(new FileInputStream(userDir + "/properties/config.properties"));
            DB_URL = prop2.getProperty("dbUrl");
            USER = prop2.getProperty("dbUserName");
            PASS = prop2.getProperty("dbPwd");
            JDBC_DRIVER = prop2.getProperty("databaseDriver");
        } catch (IOException ex) {
            LOGGER.error(ex);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * connecting to the UES and jira to get the data needed
     */
    public void jiraDetails() {

        InvokeRest invoke = new InvokeRest();
        String result = invoke.Connect("https://192.168.66.25:9447/engineering-dashboard/apis/conf/projectDetails.jag");
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //get current date time with Date()
            Date date = new Date();
            String currDate = dateFormat.format(date);
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArray = jsonObj.getJSONArray("TG");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObj1.getJSONArray("tgDetails");

                for (int j = 0; j < jsonArray1.length(); j++) {

                    JSONObject jsonObjFinal = jsonArray1.getJSONObject(j);

                    String proName = jsonObjFinal.getString("jiraKey");
                    if (!proName.equalsIgnoreCase("")) {
                        String highestUrl = "https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20" + proName + "%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20Highest&maxResults=0";
                        String highUrl = "https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20" + proName + "%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20High&maxResults=0";
                        String normalUrl = "https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20" + proName + "%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20Normal&maxResults=0";
                        String lowUrl = "https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20" + proName + "%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20Low&maxResults=0";
                        String lowestUrl = "https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20" + proName + "%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20Lowest&maxResults=0";

                        JSONObject resultHighest = new JSONObject(invoke.Connect(highestUrl));
                        JSONObject resultHigh = new JSONObject(invoke.Connect(highUrl));
                        JSONObject resultNormal = new JSONObject(invoke.Connect(normalUrl));
                        JSONObject resultLow = new JSONObject(invoke.Connect(lowUrl));
                        JSONObject resultLowest = new JSONObject(invoke.Connect(lowestUrl));

                        LOGGER.debug("*** " + proName + " ***");
                        LOGGER.debug("Highest : " + resultHighest.getInt("total"));
                        LOGGER.debug("High : " + resultHigh.getInt("total"));
                        LOGGER.debug("Normal : " + resultNormal.getInt("total"));
                        LOGGER.debug("Low : " + resultLow.getInt("total"));
                        LOGGER.debug("Lowest : " + resultLowest.getInt("total"));

                        insertToJira(jsonObjFinal.getString("pName"), jsonObjFinal.getString("jiraKey"), currDate, resultHighest.getInt("total"), resultHigh.getInt("total"), resultNormal.getInt("total"), resultLow.getInt("total"), resultLowest.getInt("total"));
                    }
                }


            }
            LOGGER.debug("*** System Updated ***");
        } catch (JSONException e) {
            LOGGER.error(e);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }


    }

    /**
     *
     * @param productName
     * @param jiraKey
     * @param date
     * @param Highest
     * @param High
     * @param Normal
     * @param Low
     * @param Lowest
     * writing the data to the database;
     */
    public void insertToJira(String productName, String jiraKey, String date, int Highest, int High, int Normal, int Low, int Lowest) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            sql = "Select * from public_jira_priority where productName='" + productName + "' AND JiraKey='" + jiraKey + "';";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                sql = "Update public_jira_priority set Date='" + date + "',countHighest='" + Highest + "',countHigh='" + High + "', countNormal='" + Normal + "', countLow='" + Low + "', countLowest='" + Lowest + "' where productName='" + productName + "' AND JiraKey='" + jiraKey + "';";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                LOGGER.debug("Successfully updated");
            } else {
                sql = "insert into public_jira_priority values('" + productName + "','" + jiraKey + "','" + Highest + "','" + High + "','" + Normal + "','" + Low + "','" + Lowest + "','" + date + "');";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                LOGGER.debug("Successfully inserted");
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }
}