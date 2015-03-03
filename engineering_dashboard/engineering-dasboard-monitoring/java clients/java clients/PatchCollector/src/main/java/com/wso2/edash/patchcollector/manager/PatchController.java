package com.wso2.edash.patchcollector.manager;

import com.wso2.edash.patchcollector.invoke.Connector;
import com.wso2.edash.patchcollector.publish.PublishData;
import com.wso2.edash.patchcollector.util.DataBaseConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yashira on 7/2/14.
 */
public class PatchController {

    private Connector connector = new Connector();
    //private Properties properties = new Properties();
    private PublishData publishData = new PublishData();
    private DataBaseConnector dbConnector;
    private List<String> userList;

    public PatchController(DataBaseConnector connector){
        this.dbConnector = connector;
        userList = getUserList();
    }


    public void countPatches(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormat.format(cal.getTime());
        String lastUpdateDate = getLastUpdateDate();

        while (!curDate.equals(lastUpdateDate)){
            String formattedDate = dateFormatter(lastUpdateDate);

            for(int i = 0;i < userList.size();i++){
                String url = "https://192.168.66.11:9444/engdash/rest/api/imp/";
                url = url.concat(userList.get(i)).concat("/"+formattedDate);
                JSONArray jsonArray = new JSONArray(connector.getData(url));
                JSONObject numberOfPatchesJson = jsonArray.getJSONObject(0);
                int z = (Integer)numberOfPatchesJson.get("NumberOfPatches");

                String patchDetails[] = { "Patches_Count",userList.get(i),lastUpdateDate, ""+z};

                try{
                    Thread.sleep(2000);
                    publishData.dataPublish(patchDetails);
                    System.out.println("URL is " + url +" number of Patches : "+z);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }
            String lastUpdatedDate = lastUpdateDate;
            lastUpdateDate = getNextDay(lastUpdateDate);

            String query = "UPDATE wes_last_update_patches SET type='PATCHES_COUNT',"+
                      "last_update_date='"+lastUpdatedDate+"',next_update_date='"+lastUpdateDate+"'";
            int status = dbConnector.executeQuery(query);
            System.out.println("************************\tEnd\t*************************");
            try{
                Thread.sleep(7200000);//Sleeping for 1 hour
            }catch (Exception ex){

            }
        }
    }

    public String getLastUpdateDate(){
        String lastUpdateDate = "2014-01-01";
        String query = "SELECT next_update_date FROM wes_last_update_patches";
        ResultSet rs = dbConnector.getResults(query);

        try {
            if(rs.next()){
                lastUpdateDate = rs.getString("next_update_date");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lastUpdateDate;
    }

    public List<String> getUserList(){
        String sqlQuery = "SELECT userEmail FROM SUPPORT_JIRA_USER_LIST";
        ResultSet rs = dbConnector.getResults(sqlQuery);
        List<String> userList = new ArrayList<String>();
        try {
            while (rs.next()) {
                userList.add(rs.getString("userEmail"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return userList;
    }

    public String dateFormatter(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        DateFormat convertDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try{
            calendar.setTime(dateFormat.parse(date));
        }catch (ParseException ex){
            System.out.println("Parser Exception occurred. " + ex.getMessage());
        }

        return convertDateFormat.format(calendar.getTime());
    }

    public String getNextDay(String curDate){
       Calendar calendar = Calendar.getInstance();
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       try {
           calendar.setTime(sdf.parse(curDate));
           calendar.add(Calendar.DATE, 1);
       }catch (ParseException ex){
           System.out.println("Parser Exception occurred. " + ex.getMessage());
       }
        return sdf.format(calendar.getTime());
    }

}
