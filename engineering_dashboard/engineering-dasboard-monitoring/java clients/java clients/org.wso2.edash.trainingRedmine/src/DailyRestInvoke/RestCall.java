package DailyRestInvoke;


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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

import Connection.InvokeRest;

public class RestCall {
 // http://localhost:8080/RESTfulExample/json/product/get
 static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/wesdb10";

    //  Database credentials
    static final String USER = "wesuser";
    static final String PASS = "4ayrzKMoF7vos";
    String result,sql;
    Connection conn = null;
    Statement stmt = null;
    String sqlRedmine="",lastUpdate=" ";
    String details[]=new String[2];


public void getCount()
        {
        try {
            InvokeRest invoke= new InvokeRest();

            result=invoke.Connect("https://stgredmine.private.wso2.com/projects/ws-sandbox/issues.json?status_id=*");
            JSONObject jsonObj = new JSONObject(result);
            String total=jsonObj.get("total_count").toString();
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

            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            sql="Update IssueCountTable SET WebinarNo="+webinar_count+",PaperNo="+paper_count+" Where Project='Marketing'";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);


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
public void getTrainingCount()

{

    try {

        InvokeRest invoke= new InvokeRest();
        String result;

        result=invoke.Connect("https://stgredmine.private.wso2.com/projects/ws-sandbox/issues.json?status_id=*");

        JSONObject jsonObj = new JSONObject(result);
        String total=jsonObj.get("total_count").toString();

        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        System.out.println("Connected to database...");
        sql="Update IssueCountTable SET TrainingNo="+total+" Where Project='Training'";
        stmt = conn.createStatement();
        stmt.executeUpdate(sql);





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


   public void getTrainings()
   {

try{
    System.out.println("starts here");
       InvokeRest invoke= new InvokeRest();
       String result;
       int issueID=0;
       String dateArray[]=new String[3];
       String arr[]=new String[3];
       String issue_created_date1,issue_created_date="";
       String issue_updated_date1,issue_updated_date="";
       String getNewDate="";
       String tracker,email="";
       String sqlUpdate,sqlInsert="";
       String[] conductedArr;
       String TG="";
       String trainingDaytemp="",trainingDay="";
       String fieldName="";
       String conducted_other="";
       int week=0, year=0,month=0,quarter=0, day=0, weekOfYear=0;
       getNewDate=getLastUpdatedDate();
       String TG_belongs="";
       String productBelongs="";


       int val=0;


       result=invoke.Connect("https://redmine.wso2.com/projects/wso2-training/issues.json?updated_on="+getNewDate+"&status_id=*");
      

       JSONObject jsonObj = new JSONObject(result);
       JSONArray jsonArray = jsonObj.getJSONArray("issues");

    for(int i=0;i<jsonArray.length(); i++)
    {
        JSONObject objectInArray = jsonArray.getJSONObject(i);
        issueID=objectInArray.getInt("id");

        issue_created_date1=objectInArray.get("created_on").toString();
        issue_created_date=convertDate(issue_created_date1);
        issue_updated_date1=objectInArray.get("updated_on").toString();
        issue_updated_date=convertDate(issue_updated_date1);
        tracker=objectInArray.getJSONObject("tracker").get("name").toString();

        JSONArray custom=objectInArray.getJSONArray("custom_fields");

      for(int h=0; h<custom.length(); h++){

            JSONObject objTG=custom.getJSONObject(h);
            fieldName= objTG.getString("name");
          if(fieldName.equalsIgnoreCase("Select_TG"))
          {
            TG=objTG.get("value").toString();

          }

          else if(fieldName.equalsIgnoreCase("training day"))
          {
              trainingDaytemp=objTG.get("value").toString();
              trainingDay=convertDate(trainingDaytemp);

              Calendar training_cal=Calendar.getInstance();
              training_cal.setTime((new java.text.SimpleDateFormat("yyyy-MM-dd")).parse(trainingDay));
              week=training_cal.get(Calendar.WEEK_OF_MONTH);
              weekOfYear=training_cal.get(Calendar.WEEK_OF_YEAR);

              dateArray=splitdate(trainingDay);
              month=Integer.parseInt(dateArray[1]);
              year=Integer.parseInt(dateArray[0]);
              day=Integer.parseInt(dateArray[2]);
              quarter=getQuarter(month);

              
          }

          else if(fieldName.equalsIgnoreCase("Conducted By (Other)"))
          {
             conducted_other=objTG.get("value").toString();

          }
          else{}

      }

        for(int p=0;p<custom.length();p++){

            JSONObject obj=custom.getJSONObject(p);
            fieldName=obj.getString("name");

        if(fieldName.equalsIgnoreCase("conducted by"))
          {
            JSONObject objCond=custom.getJSONObject(p);
            JSONArray js=objCond.getJSONArray("value");

              if(js.length()==0)
              {
                  if(conducted_other.equalsIgnoreCase(""))
                  {
                    email="";
                      TG_belongs="OTHER";
                      productBelongs="OTHER";
                    insertTotraining(issueID, trainingDay, week,weekOfYear, quarter, day, month, year, issue_created_date, issue_updated_date, tracker, email, TG,TG_belongs,productBelongs);

                  }
                  else{
                  email=conducted_other;
                  details=getDetails(email);
                  TG_belongs=details[0];
                  productBelongs=details[1];



                  insertTotraining(issueID, trainingDay, week,weekOfYear, quarter, day, month, year, issue_created_date, issue_updated_date, tracker, email, TG,TG_belongs,productBelongs);
              }
              }

else{
                for(int s=0; s < js.length(); s++)
                {
                    conductedArr=new String[js.length()];
                    val=Integer.parseInt(js.get(s).toString());

                    result=invoke.Connect("https://redmine.wso2.com/users/"+val+".json");

                    JSONObject jsonObjuser = new JSONObject(result);
                    JSONObject jUser=jsonObjuser.getJSONObject("user");
                    email=jUser.getString("mail").toString();

                    details=getDetails(email);
                    TG_belongs=details[0];
                    productBelongs=details[1];

                    insertTotraining(issueID, trainingDay, week,weekOfYear, quarter, day, month, year, issue_created_date, issue_updated_date, tracker, email, TG,TG_belongs,productBelongs);



                }
          }


      }
        }

        //add the other for loop end
        System.out.println("Record Added");


    }

} 

catch (JSONException ex)
{




    ex.printStackTrace();
}
       catch (Exception e)
       {

           e.printStackTrace();
       }

       updateTable();



   }

public String convertDate(String dateString) throws ParseException {

 String[] arr=dateString.split("T");
 String date=arr[0];

 return date;

}


public String getLastUpdatedDate()
{
    String lastUpdate="";
    try {
        conn = DriverManager.getConnection(DB_URL,USER,PASS);

        sql="Select * from LastUpdatedRedmine where category='Training'";
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
        sqlRedmine="UPDATE LastUpdatedRedmine set last_updated_date='"+date+"' where Category='Training'";
        stmt = conn.createStatement();
        stmt.executeUpdate(sqlRedmine);
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

    public void insertTotraining(int issueID,String trainingDay,int weekNo,int weekinYear,int quarter, int day, int month, int year,String created_date, String updated_date,String tracker,String user,String TG, String TG_Belongs,String Platform_belongs)
    {
        try{

        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        //System.out.println("Connected to database...");
        sql="Select * from RedmineTraining where issueID="+issueID;
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next())
        {

            sql="Update RedmineTraining set created_date='"+created_date+"',updated_date='"+updated_date+"',trainingDay='"+trainingDay+"',tracker='"+tracker+"',user='"+user+"',TG='"+TG+"',week_of_month="+weekNo+",week_of_year="+weekinYear+",quarter="+quarter+",year="+year+",day="+day+",month="+month+",TG_Belongs='"+TG_Belongs+"',Product_belongs='" +Platform_belongs+"' where issueID="+issueID;
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        }

        else
        {

            sql="insert into RedmineTraining values("+issueID+",'"+trainingDay+"',"+weekNo+","+weekinYear+","+quarter+","+day+","+month+","+year+",'"+created_date+"','"+updated_date+"','"+tracker+"','"+user+"','"+TG+"','"+TG_Belongs+"','"+Platform_belongs+"')";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);


        }
        } catch (SQLException e)
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




