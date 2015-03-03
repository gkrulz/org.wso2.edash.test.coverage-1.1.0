package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shenavi on 5/21/14.
 */
public class invokeRest {


    public invokeRest()
    {}

    public String Connect(String invokeURL)

    {
        String output="";
        try{
            String username="wedb-prod-user@wso2.com";
            String password="o3dO5gxvqkYuI";
            String userpass = username + ":" + password;
            URL url = new URL(invokeURL);
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn .setRequestProperty ("Authorization", basicAuth);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            output=br.readLine();
            //return output;

        }
        catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return output;


    }


}