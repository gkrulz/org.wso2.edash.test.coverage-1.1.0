package database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.edash.redmine.newVersion.log.Log;
import schedule.Issue;
import schedule.RedmineRestApiCall;
import schedule.Release;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Database {

    private String db_url = "";
    private String db_username = "";
    private String db_password = "";
    private String db_driver = "";
    private Connection connection;
    private PreparedStatement preparedStatement;

    static final Logger LOGGER = LogManager.getLogger(Database.class.getName());

    public Database() {
        connectToDatabase();
    }

    public void connectToDatabase() {

        Properties prop = new Properties();

        try {
            // load a properties file
            prop.load(RedmineRestApiCall.class.getClassLoader().getResourceAsStream("config.properties"));

            db_url = prop.getProperty("dbUrl");
            db_username = prop.getProperty("dbUserName");
            db_password = prop.getProperty("dbPwd");
            db_driver = prop.getProperty("databaseDriver");
            Class.forName(db_driver).newInstance();
            connection = DriverManager.getConnection(db_url, db_username, db_password);

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

    public void insertDB(String productName, Release release) {

        LOGGER.debug("Inserting Data to Database");

        String sql = "INSERT INTO REDMINE_DETAILS ( project_name,next_realese,new,open,closed,estimated,actual_open,actual_closed,r_date,dencity,done_ratio,carbon_version,start_date,old_done_ratio) VALUES('"
                + productName
                + "','"
                + release.getName()
                + "',"
                + release.getNewIssuesCount()
                + ","
                + release.getIsIssuesCount()
                + ","
                + release.getClosedIssuesCount()
                + ",'"
                + release.getEssimated()
                + "','"
                + release.getActual_open()
                + "','"
                + release.getActual_closed()
                + "','"
                + release.getEndDate()
                + "','"
                + release.getDencity()
                + "','"
                + release.getPercentage()
                + "','"
                + release.getCarbonVersion()
                + "','"
                + release.getStartdate()
                + "','"
                + release.getOld_done_ratio() + "');";


        try {
            Class.forName(db_driver).newInstance();

            Connection con = DriverManager.getConnection(db_url, db_username,
                    db_password);

            preparedStatement = con.prepareStatement(sql);

            //
            // preparedStatement.setString(1, productName);
            //
            // preparedStatement.setString(2, release.getSubject());
            // preparedStatement.setInt(3, release.getNewIssuesCount());
            //
            // preparedStatement.setInt(4, release.getIsIssuesCount());
            // preparedStatement.setInt(5, release.getClosedIssuesCount());
            //
            // preparedStatement.setInt(6,
            // Integer.parseInt(release.getEssimated()));
            // preparedStatement.setInt(7,
            // Integer.parseInt(release.getActual_open()));
            // preparedStatement.setDouble(8,
            // Integer.parseInt(release.getActual_closed()));
            // preparedStatement.setString(9, release.getEndDate());
            //
            // preparedStatement.setInt(10,
            // Integer.parseInt(Integer.toString(release.getDencity())));
            // preparedStatement.setString(11, release.getPercentage());
            // System.out.println(release.getPercentage());
            preparedStatement.executeUpdate();


            //Add Issues


            if (!con.getAutoCommit()) {
                con.commit();
            }


            con.close();
            LOGGER.debug("Table REDMINE_DETAILS updated");


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    public String doneRatio(String project_name, String next_realese) {

        ResultSet resultSet;
        String sql = "SELECT project_name,next_realese,done_ratio FROM REDMINE_DETAILS_COPY WHERE project_name=? AND next_realese=?";
        String done_ratio = new String();
        try {
            Class.forName(db_driver);
            Connection conn = DriverManager.getConnection(db_url, db_username,
                    db_password);

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, project_name);
            preparedStatement.setString(2, next_realese);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                done_ratio = resultSet.getString("done_ratio");
            }
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return done_ratio;

    }

    public void insertIssueDetails(String productName, String releaseName, Issue issue) {

        PreparedStatement pstmt = null;

        try {
            Class.forName(db_driver).newInstance();

            Connection con = DriverManager.getConnection(db_url, db_username, db_password);

           /* String issueSql = "INSERT INTO REDMINE_ISSUES_DETAILS (project_name,assignee,realese_name,issue_id,milestone,"
                    + "issue_description,issue_status,issue_edays,issue_adays) VALUES('"
                    + productName + "','"
                    + issue.getAssignee() + "','"
                    + releaseName + "','"
                    + issue.getKey() + "','"
                    + issue.getMilestone() + "','"
                    + issue.getDescription() + "','"
                    + issue.getStatus() + "', "
                    + issue.getEstmatedHours() / 24 + ","
                    + issue.getActual_open() + ");";
                    Statement statement = con.createStatement();
            statement.executeUpdate(issueSql);

*/
//queries modified
            String tempQuery = "INSERT INTO REDMINE_ISSUES_DETAILS (project_name,assignee,realese_name,issue_id,milestone,"
                    + "issue_subject,issue_description,issue_status,issue_edays,issue_adays,issue_priority) VALUES(?,?,?,?,?,?,?,?,?,?,?)";


            String query = "INSERT INTO REDMINE_ISSUES_DETAILS (project_name,assignee,realese_name,issue_id,milestone,"
                    + "issue_subject,issue_status,documentation_link,issue_edays,issue_adays,issue_priority) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt = con.prepareStatement(query); // create a statement
            pstmt.setString(1, productName);
            pstmt.setString(2, issue.getAssignee());
            pstmt.setString(3, releaseName);
            pstmt.setString(4, issue.getKey());
            pstmt.setString(5, issue.getMilestone());
            pstmt.setString(6, issue.getSubject());
            //pstmt.setString(7, issue.getDescription()); // no need to save this
            pstmt.setString(7, issue.getStatus());
            pstmt.setString(8, issue.getDocumentationLink());
            pstmt.setDouble(9, issue.getEstmatedHours() / 24);
            pstmt.setInt(10, issue.getActual_open());
            //modified
            pstmt.setString(11, issue.getPriority());
            //LOGGER.debug("ISSUE SQL =====> {}", pstmt.toString());
            pstmt.executeUpdate(); // execute insert statement
            LOGGER.debug("Issue {} added to Database", issue.getKey());
            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

        } catch (InstantiationException e) {
            Log.LOGGER.error(e);
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            Log.LOGGER.error(e);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            Log.LOGGER.error(e);
        } catch (SQLException e) {
            //e.printStackTrace();
            Log.LOGGER.error(e);
        } catch (Exception e) {
            //e.getMessage();
            //e.printStackTrace();
            Log.LOGGER.error(e);
        }


    }

    public void deleteOrginalTables() {

        String sql = "DELETE FROM REDMINE_DETAILS;";
        String delIssues = "DELETE FROM REDMINE_ISSUES_DETAILS;";

        try {
            Class.forName(db_driver).newInstance();

            Connection con = DriverManager.getConnection(db_url, db_username,
                    db_password);

            preparedStatement = con.prepareStatement(sql);
            preparedStatement.execute();

            preparedStatement = con.prepareStatement(delIssues);
            preparedStatement.execute();


            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

            LOGGER.debug("REDMINE_DETAILS AND REDMINE_ISSUES_DETAILS DELETED!");

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    public void deleteDb() {

        String sql = "DROP TABLE IF EXISTS REDMINE_DETAILS_COPY;";
        String issuesBackupSql = "DROP TABLE IF EXISTS REDMINE_ISSUES_DETAILS_COPY;";

        try {
            Class.forName(db_driver).newInstance();

            Connection con = DriverManager.getConnection(db_url, db_username, db_password);
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.execute();

            LOGGER.debug("REDMINE_DETAILS_COPY Table deleted");
            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    public void cpTable() {

        String sql = "CREATE TABLE IF NOT EXISTS REDMINE_DETAILS_COPY AS (SELECT * FROM REDMINE_DETAILS);";
        String issuesBackupSql = "CREATE TABLE IF NOT EXISTS REDMINE_ISSUES_DETAILS_COPY AS (SELECT * FROM REDMINE_ISSUES_DETAILS);";


        try {
            Class.forName(db_driver).newInstance();
            Connection con = DriverManager.getConnection(db_url, db_username, db_password);
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            LOGGER.debug("REDMINE_DETAILS Backup created");

            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }


}
