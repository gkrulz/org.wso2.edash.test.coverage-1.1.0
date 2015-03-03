package schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;


import connection.invokeRest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;


/**
 * Created by shenavi on 5/21/14.
 */
public class RestApiCall {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/wesdb10";

    //  Database credentials
    static final String USER = "wesuser";
    static final String PASS = "4ayrzKMoF7vos";
    String result,sql,checkSql;
    Connection conn = null;
    Statement stmt = null;
    int count=0;
    String belongs[]=new String[2];
   public RestApiCall()
   {}

    public void getCount()
    {
        try {
            invokeRest invoke= new invokeRest();
            String dateUpdated=getLastUpdatedDate();
            result=invoke.Connect("https://stgredmine.private.wso2.com/projects/ws-sandbox/issues.json?status_id=*");
            System.out.println(result);
            //filterJson.FilterJson(output);
            JSONObject jsonObj = new JSONObject(result);
            String total=jsonObj.get("total_count").toString();
            System.out.println("total is"+ total);
            int webinar_count=0;
            int paper_count=0;
            JSONArray jsonArray = jsonObj.getJSONArray("issues");
            for (int i = 0, size = jsonArray.length(); i < size; i++)
            {
                System.out.println(jsonArray.length());

                JSONObject objectInArray = jsonArray.getJSONObject(i);
                JSONObject tracker = objectInArray.getJSONObject("tracker");
                String type=tracker.getString("name").toString();
                //System.out.println(tracker);
                //System.out.println("the type is "+type);

                if (type.equalsIgnoreCase("webinar"))
                {
                    webinar_count++;
                }

                if (type.equalsIgnoreCase("White Paper"))
                {
                    paper_count++;
                }


            }
            System.out.println("Paper count is "+ paper_count);
            System.out.println("Webinar count is "+ webinar_count);

            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connected to database...");
            sql="Update IssueCountTable SET WebinarNo="+webinar_count+",PaperNo="+paper_count+" Where Project='Marketing'";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Success");


        } catch (JSONException e) {

            e.printStackTrace();

        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (Exception e) {

            e.printStackTrace();

        }
    }


    public void getMarketingDetails()
    {
        invokeRest invoke= new invokeRest();
        String tracker="";
        String Title="";
        String tempDate="";
        String Date="";
        String TG="";
        String Team="";
        String Presenter="";
        String result="";
        String dateArray[]= new String[3];
        String fieldName="";
        String email="";
        String conducted_other="";
        int Day=0;
        int Month=0;
        int Year=0;
        int Quarter=0;
        int Registrants=0;
        int Attendants=0;
        int week=0;
        int val=0;
        int IssueID=0;
        int weekOfYear=0;
        String tg_belongs="";
        String product_belongs="";

        try{
            String getNewDate=getLastUpdatedDate();
            System.out.println(getNewDate);
            result=invoke.Connect("https://redmine.wso2.com/projects/ws-sandbox/issues.json?updated_on="+getNewDate+"&status_id=*");
            
            System.out.println(result);
        //filterJson.FilterJson(output);
        JSONObject jsonObj = new JSONObject(result);
        JSONArray jsonArray = jsonObj.getJSONArray("issues");

         for(int i=0; i<jsonArray.length();i++)
         {

             JSONObject objectInArray = jsonArray.getJSONObject(i);
             System.out.println(objectInArray);
             Title=objectInArray.get("subject").toString();
             System.out.println("the title is *"+Title);
             tempDate=objectInArray.get("start_date").toString();
             Date=convertDate(tempDate);
             System.out.println("the date is *"+Date);
             IssueID=objectInArray.getInt("id");
             System.out.println("the id is *"+IssueID);

             checkSql="Select * from RedmineMarketing where issueID="+IssueID+";";
            

             Calendar end_cal=Calendar.getInstance();
             end_cal.setTime((new java.text.SimpleDateFormat("yyyy-MM-dd")).parse(Date));
             week=end_cal.get(Calendar.WEEK_OF_MONTH);
             weekOfYear=end_cal.get(Calendar.WEEK_OF_YEAR);

             System.out.println("the week number is *"+week);

             dateArray=splitdate(Date);
             Month=Integer.parseInt(dateArray[1]);
             Year=Integer.parseInt(dateArray[0]);
             Day=Integer.parseInt(dateArray[2]);
             Quarter=getQuarter(Month);

             System.out.println("year is "+ Year);
             System.out.println("month is "+ Month);
             System.out.println("day is "+ Day);
             System.out.println("quarter is "+ Quarter);



             tracker=objectInArray.getJSONObject("tracker").get("name").toString();
             System.out.println("The tracker is *"+tracker);

             if(tracker.equalsIgnoreCase("webinar"))
             {
                 JSONArray customwebi=objectInArray.getJSONArray("custom_fields");
                 for(int k=0;k<customwebi.length();k++)
                 {
                 JSONObject objTG=customwebi.getJSONObject(k);
                 fieldName=objTG.getString("name");

                     if(fieldName.equalsIgnoreCase("TG"))
                     {
                 TG=objTG.get("value").toString();
                 System.out.println("The tg is xx"+ TG);
                     }
                     else if(fieldName.equalsIgnoreCase("number of participants"))
                     {
                         Attendants=objTG.getInt("value");
                         System.out.println("The Attendants no is"+ Attendants);
                     }

                     else if(fieldName.equalsIgnoreCase("number of registrants") )
                     {
                      Registrants=objTG.getInt("value");
                         System.out.println("The Registrants no is"+ Registrants);
                     }

                     else if(fieldName.equalsIgnoreCase("related team"))
                     {
                         Team=objTG.get("value").toString();
                         System.out.println("The team is"+ Team);

                     }

                     else if(fieldName.equalsIgnoreCase("Conducted By (Other)"))
                     {
                         conducted_other=objTG.get("value").toString();

                     }
                     else {}

                 }

                 for(int m=0;m<customwebi.length();m++)
                 {
                     JSONObject obj=customwebi.getJSONObject(m);
                     fieldName=obj.getString("name");
                     if(fieldName.equalsIgnoreCase("conducted by"))
                     {


                         JSONArray js=obj.getJSONArray("value");
                         if(js.length()==0)
                         {
                             if(conducted_other.equalsIgnoreCase(""))
                             {
                                 email="";
                                 tg_belongs="OTHER";
                                 product_belongs="OTHER";
                                 System.out.println(tg_belongs);
                                 System.out.println(product_belongs);

                                 insertRecord(IssueID, tracker, Title, Date, Day, Month, Year, Quarter, week,weekOfYear, TG, Team, email, Attendants, Registrants,tg_belongs,product_belongs);

                             }
                             else{
                                 email=conducted_other;
                                 tg_belongs="OTHER";
                                 product_belongs="OTHER";
                                 System.out.println(tg_belongs);
                                 System.out.println(product_belongs);
                                 insertRecord(IssueID, tracker, Title, Date, Day, Month, Year, Quarter, week,weekOfYear, TG, Team, email, Attendants, Registrants,tg_belongs,product_belongs);

                             }
                         }


                        else{
                         for(int g=0; g<js.length();g++)
                         {
                             val=Integer.parseInt(js.get(0).toString());
                             System.out.println("User number is" + val);

                             result=invoke.Connect("https://stgredmine.private.wso2.com/users/"+val+".json");
                             // System.out.println(result);
                             //filterJson.FilterJson(output);
                             JSONObject jsonObjuser = new JSONObject(result);
                             JSONObject jUser=jsonObjuser.getJSONObject("user");
                             email=jUser.getString("mail").toString();
                             System.out.println("the email is" + email);
                             belongs=getDetails(email);
                             tg_belongs=belongs[0];
                             product_belongs=belongs[1];
                             System.out.println(tg_belongs);
                             System.out.println(product_belongs);
                             System.out.println(IssueID);
                             System.out.println(tracker);
                             System.out.println(Title);
                             System.out.println(Date);
                             System.out.println(Day);
                             System.out.println(Month);
                             System.out.println(Year);
                             System.out.println(Quarter);
                             System.out.println(week);
                             System.out.println(weekOfYear);
                             System.out.println(TG);
                             System.out.println(Team);
                             System.out.println(email);
                             System.out.println(Attendants);
                             System.out.println(Registrants);



                             insertRecord(IssueID, tracker, Title, Date, Day, Month, Year, Quarter, week,weekOfYear, TG, Team, email, Attendants, Registrants,tg_belongs,product_belongs);


                         }

                     }

                     }
                 }




             }

             else if(tracker.equalsIgnoreCase("white paper"))
             {
                 System.out.println("Category is white paper");
                 JSONArray customwp=objectInArray.getJSONArray("custom_fields");
                 for(int l=0;l<customwp.length();l++)
                 {
                     JSONObject objTG=customwp.getJSONObject(l);
                     fieldName=objTG.getString("name");

                     if(fieldName.equalsIgnoreCase("TG"))
                     {
                         TG=objTG.get("value").toString();
                         System.out.println("The tg is"+ TG);
                     }


                     if(fieldName.equalsIgnoreCase("related team"))
                     {
                         Team=objTG.get("value").toString();
                         System.out.println("The team is"+ Team);
                     }



                 }
                 tg_belongs="OTHER";
                 product_belongs="OTHER";
                 Attendants=0;
                 Registrants=0;

                 System.out.println(tg_belongs);
                 System.out.println(IssueID);
                 email="";

                 insertRecord(IssueID, tracker, Title, Date, Day, Month, Year, Quarter, week,weekOfYear, TG, Team, email, Attendants, Registrants,tg_belongs,product_belongs);

             }


else{}

         }



}
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        catch(Exception ex)

        {

            ex.printStackTrace();
        }

        updateTable();
    }




    public String convertDate(String dateString) throws ParseException {

        String[] arr=dateString.split("T");
        String date=arr[0];
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //java.util.Date newDate=dateFormat.parse(date);
        return date;

    }



    public String getLastUpdatedDate()
    {
        String lastUpdate="";
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connected to database...");
            sql="Select * from LastUpdatedRedmine where category='Marketing'";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                lastUpdate=resultSet.getString("last_updated_date");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);  // number of days to add
            lastUpdate = sdf.format(c.getTime());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastUpdate;

    }

    public String[] splitdate(String date)
    {
        String[] dateArr=date.split("-");
        return dateArr;

    }



    public int getQuarter(int month)
    {
        int quarter=0;
        if(month>=1 && month <=3)
        {
            quarter=1;
        }
        else if(month>=4 && month <=6)
        {
            quarter=2;
        }
        else if(month>=7 && month <=9)
        {
            quarter=3;
        }
        else if(month>=10 && month <=12)
        {
            quarter=4;
        }
        else {

        }
        return quarter;

    }

    public void updateTable()
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        try{
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            sql="UPDATE LastUpdatedRedmine set last_updated_date='"+date+"' where Category='Marketing'";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Successfully update redmine last updated");
        }

        catch (SQLException e)
        {
            e.printStackTrace();

        }

        catch (Exception ex)
        {
            ex.printStackTrace();

        }



    }


public void insertRecord(int id,String tracker, String title, String date, int day, int month, int year, int quarter, int weekNo,int weekInYEar,String TG, String team,String presenter, int attended, int registered,String TG_belongs,String product_belongs)
{
count++;


    checkSql="Select * from RedmineMarketing where issueID="+id+";";
    try{
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
    PreparedStatement preparedStatement = conn.prepareStatement(checkSql);
    ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next())
        {
            sql="UPDATE RedmineMarketing SET Tracker='"+tracker+"',Title='"+title+"',Date='"+date+"',Day="+day+",Month="+month+",Year="+year+",WeekOfMonth="+weekNo+",WeekOfYear="+weekInYEar+",Quarter="+quarter+",TG='"+TG+"',relatedTeam='"+team+"',Presenter='"+presenter+"',AttendedNo="+attended+",RegisteredNo="+registered+",TG_Belongs='"+TG_belongs+"',Product_Belongs='"+product_belongs+"' where issueID="+id+";";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Successfully updated");

        }

    else{

    sql="INSERT INTO RedmineMarketing VALUES("+id+",'"+tracker+"','"+title+"','"+date+"',"+day+","+month+","+year+","+weekNo+","+weekInYEar+","+quarter+",'"+TG+"','"+team+"','"+presenter+"',"+attended+","+registered+",'"+TG_belongs+"','"+product_belongs+"');";

    stmt = conn.createStatement();
    stmt.executeUpdate(sql);
    System.out.println("Successfully inserted");
        }

    }

    catch (SQLException e)
    {
        e.printStackTrace();

    }

    catch (Exception ex)
    {
        ex.printStackTrace();

    }

}


    public String[] getDetails(String email)
    {
        String arr[]= new String[2];
        try{

            String TGBelongs="";
            String ProductBelongs="";
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            //System.out.println("Connected to database...");
            sql="Select productName,tgName from SUPPORT_JIRA_USER_LIST where userEmail='"+email+"'";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                arr[0]=resultSet.getString("tgName");
                arr[1]=resultSet.getString("productName");
                System.out.println(arr[0]);
                System.out.println(arr[1]);


            }

            else
            {

                arr[0]="OTHER";
                arr[1]="OTHER";



            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return arr;

    }






}