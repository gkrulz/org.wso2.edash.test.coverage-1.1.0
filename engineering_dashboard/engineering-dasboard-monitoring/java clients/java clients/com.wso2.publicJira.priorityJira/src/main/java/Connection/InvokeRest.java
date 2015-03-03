package Connection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Padmaka Wijayagoonawardena on 10/13/14.
 * Email - padmakaj@wso2.com
 */

/**
 * Class for invoking rest calls
 */
public class InvokeRest {
    public static final Logger LOGGER = LogManager.getLogger(InvokeRest.class.getName());

    public InvokeRest() {
    }

    /**
     * @param invokeURL
     * @return The method to connect to the UES and public Jira
     */
    public String Connect(String invokeURL) {

        String output = "";
        try {
            URL url = new URL(invokeURL);
            String currentDir = System.getProperty("user.dir");
            System.setProperty("javax.net.ssl.trustStore", currentDir + "/properties/client-truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            output = br.readLine();

        } catch (MalformedURLException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return output;
    }
}

	
