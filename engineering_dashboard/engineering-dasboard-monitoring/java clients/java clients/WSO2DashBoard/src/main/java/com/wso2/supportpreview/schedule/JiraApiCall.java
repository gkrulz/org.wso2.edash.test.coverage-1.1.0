package com.wso2.supportpreview.schedule;

import com.wso2.supportpreview.invoke.JiraRestApiInvoke;
import com.wso2.supportpreview.publish.ResolveCounter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.zip.InflaterInputStream;

/**
 * Created by yashira on 5/5/14.
 */
public class JiraApiCall {
    private Properties properties = new Properties();
    private JiraRestApiInvoke jiraRestApiInvoke = new JiraRestApiInvoke();

    /*DB stuff goes here*/
    PreparedStatement preparedStatement;
    Connection con;
    String username = "";
    String password = "";
    String databaseDriver = "";
    String dburl = "";
    Logger logger;
    String userDir = "";

    public JiraApiCall() throws Exception{
        logger = org.slf4j.LoggerFactory.getLogger(JiraApiCall.class);
        userDir = System.getProperty("user.dir");
        getProperties();

    }



    public void getProperties(){
        try{
            /*commented lines should be uncomment when developing*/
            //properties.load(getClass().getClassLoader().getResourceAsStream("resolveCounter_constants.properties"));
            properties.load(new FileInputStream(userDir+"/properties/resolveCounter_constants.properties"));

            Properties prop2 = new Properties();
            //prop2.load(getClass().getClassLoader().getResourceAsStream("resolveCounter_config.properties"));
            prop2.load(new FileInputStream(userDir+"/properties/resolveCounter_config.properties"));

            dburl =  prop2.getProperty("dbUrl");
            username =  prop2.getProperty("dbUserName");
            password =  prop2.getProperty("dbPwd");
            databaseDriver =  prop2.getProperty("databaseDriver");

            Class.forName(databaseDriver).newInstance();

            con = DriverManager.getConnection(dburl,username,password);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (SQLException sql){
            System.out.println("SQL Exception thrown"+sql.getMessage());
            sql.printStackTrace();
        }
        catch (Exception ex){
            System.out.println("Exception : "+ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void getResolved(){
        String curDate = "";
        String dayBefore = getLastDate();
        String nextDay = "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        curDate = dateFormat.format(cal.getTime());
        System.out.print("Current date : "+curDate);
        System.out.println("\tDay BEfore : "+dayBefore);
    try{
        Calendar cal2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal2.setTime(sdf.parse(dayBefore));
        cal2.add(Calendar.DATE, 1);
        nextDay = dateFormat.format(cal2.getTime());



        while(!curDate.equals(dayBefore)){

            String selectUserQuery = "Select * from SUPPORT_JIRA_USER_LIST";
            preparedStatement = con.prepareStatement(selectUserQuery);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Object []obj =  {dayBefore,nextDay,rs.getString("userEmail")};
                String createdUrl = properties.getProperty("SUPPORT_JIRA_URL");
                String createdIssue_userUrl = MessageFormat.format(createdUrl, obj);
                getStringJson(createdIssue_userUrl, rs.getString("userEmail"), dayBefore, nextDay);
            }
            System.out.println("******************************************************************************"+
            "****************************************************************");
            dayBefore = nextDay;
            cal2.setTime(sdf.parse(nextDay));
            cal2.add(Calendar.DATE, 1);
            nextDay = dateFormat.format(cal2.getTime());

        }
    }catch (Exception ex){
        System.out.println(ex.getMessage());
        ex.printStackTrace();
    }


    }

    public String getLastDate(){
       String query =  "SELECT dayafter_last_update FROM wes_lastSupportJiraRestAPICall ORDER BY last_update_date desc LIMIT 1";
       String output = "";
       try{
           preparedStatement = con.prepareStatement(query);
           ResultSet rs = preparedStatement.executeQuery();
           while (rs.next()){
               output = rs.getString("dayafter_last_update");
           }
       }catch(Exception ex){
           ex.printStackTrace();
       }
        return output;
    }

    public void getStringJson(String createdIssue_userUrl,String mail,String timeStamp,String nextDaytimeStamp){
        try{

            JSONObject createdIssuesJson = new JSONObject(jiraRestApiInvoke.invoke(createdIssue_userUrl));

            String userIssues[] = { "jira_resolve",mail,timeStamp,createdIssuesJson.get("total").toString()};

            ResolveCounter counter = new ResolveCounter();

            try {
                counter.dataPublish(userIssues);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (AgentException e) {
                e.printStackTrace();
            } catch (AuthenticationException e) {
                e.printStackTrace();
            } catch (TransportException e) {
                e.printStackTrace();
            } catch (MalformedStreamDefinitionException e) {
                e.printStackTrace();
            } catch (StreamDefinitionException e) {
                e.printStackTrace();
            } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
                e.printStackTrace();
            }
            System.out.println("\tLast update user : "+mail+" Last updated date is :"+timeStamp);
            String updateQuery = "update  wes_lastSupportJiraRestAPICall set last_update_date= '"+timeStamp+"'," +
                    "dayafter_last_update='"+nextDaytimeStamp+"',last_updated_user='"+mail+"' where schedule_task = 'SupportJira';";

            try{
                preparedStatement = con.prepareStatement(updateQuery);
                preparedStatement.executeUpdate();
            }catch(SQLException ex){
                System.out.println("SQL Exception");
                ex.printStackTrace();

            }


        }
        catch (NullPointerException nul){
            nul.printStackTrace();
        }
    }
}
