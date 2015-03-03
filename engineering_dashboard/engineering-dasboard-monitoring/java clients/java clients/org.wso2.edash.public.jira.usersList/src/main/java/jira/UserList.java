package jira;

/**
 * Created with IntelliJ IDEA.
 * User: anushka
 * Date: 10/16/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
public class UserList {

    String url = "jdbc:mysql://localhost:3306/";
    String dbName = "wesdb09";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "root";
    String password = "root";
   public void getAllUsers(){
        try {

            int loopCount = (total()/10)+1;
            int start =0;
            int max = 10;
            for (int j = 0 ;j<loopCount;j++ ){
                System.out.println("start"+start);
                String output = urlConnecter(start,max);
                System.out.println(output);
                JSONObject json = new JSONObject(output);

                System.out.println(loopCount);
                JSONArray issus = null;
                issus =  json.getJSONArray("issues");
                JSONObject fields = null;
                for (int i =0;i<issus.length();i++){
                      fields = issus.getJSONObject(i);
                    try{
                       String reporter = fields.getJSONObject("fields").getJSONObject("reporter").getString("emailAddress");
                        String assignee = null;

                        assignee= fields.getJSONObject("fields").getJSONObject("assignee").getString("emailAddress");
                        if(assignee.contains("@wso2.com"))   {

                            databaseConnecter(assignee);

                            System.out.println(assignee+"*******");
                        }



                        if(reporter.contains("@wso2.com"))   {

                            databaseConnecter(reporter);
                            System.out.println(reporter);
                        }
                    }
                    catch (JSONException e)  {

                    }

                }
                start +=10;
            }

        } catch (Exception e) {

            e.printStackTrace();

      }



    }
    public String urlConnecter(int start,int max){
        Client client = Client.create();

        String url = "https://wso2.org/jira/rest/api/2/search?jql=&startAt="+start+"&maxResults="+max+"&fields=reporter,assignee";
        WebResource webResource = client
                .resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        return output;
    }
    public  int total(){
        int start = 0;
        int max = 0;
        int out = 0;
        String output = urlConnecter(start,max);
        try{
            JSONObject json = new JSONObject(output);

            System.out.println(json.get("total").toString());
            out = Integer.parseInt(json.get("total").toString());

        }
        catch (JSONException e){

        }
        return  out;
    }
    public void databaseConnecter(String a){


        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url+dbName,userName,password);
            Statement st = conn.createStatement();
            String quary1 = "CREATE TABLE IF NOT EXISTS jiraUsers(id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), name VARCHAR(30));";
            st.executeUpdate(quary1);
            String quary ="INSERT INTO jiraUsers (name) SELECT * FROM (SELECT '"+a+"') AS tmp WHERE NOT EXISTS (SELECT name FROM jiraUsers WHERE name = '"+a+"') LIMIT 1;";
            st.executeUpdate(quary);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
