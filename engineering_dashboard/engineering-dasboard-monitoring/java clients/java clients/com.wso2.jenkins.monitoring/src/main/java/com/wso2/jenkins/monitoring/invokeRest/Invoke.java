package com.wso2.jenkins.monitoring.invokeRest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import com.wso2.jenkins.monitoring.db.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Padmaka Wijayagoonawardena on 1/19/15.
 * Email - padmakaj@wso2.com
 */
public class Invoke {
    public static final Logger LOGGER = LogManager.getLogger(Invoke.class.getName());
    private Properties prop;
    private ClientResponse response;
    private String output;
    private String jenkinsUserName;
    private String jenkinsPassword;
    private String jenkinsURL;
    private DatabaseConnection db;

    public Invoke(){
        prop = new Properties();
        db = new DatabaseConnection();
    }

    public String getInfo(){
        JSONArray jenkinsData = new JSONArray();

        try {
            prop.load(Invoke.class.getClassLoader()
                    .getResourceAsStream("constants.properties"));
            jenkinsUserName = prop.getProperty("JENKINS_USERNAME");
            jenkinsPassword = prop.getProperty("JENKINS_PASSWORD");
            jenkinsURL = prop.getProperty("JENKINS_URL");

            Client client = Client.create();

            String auth = new String(Base64.encode(jenkinsUserName + ":"
                    + jenkinsPassword));
            WebResource webResource = client.resource(jenkinsURL);

            response = webResource.header("Authorization", "Basic " + auth)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 500) {
                    LOGGER.debug("Failed : HTTP error code : "
                            + response.getStatus());
                    LOGGER.debug(jenkinsURL);
                }
                LOGGER.debug("Error");
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            output = response.getEntity(String.class);
            JSONObject obj = new JSONObject(output);

            for(int i = 0; i < obj.getJSONArray("jobs").length(); i++){
                String TempString = obj.getJSONArray("jobs").get(i).toString();
                JSONObject job = new JSONObject(TempString);
                String jobName = job.get("name").toString();

                webResource = client.resource("https://wso2.org/jenkins/job/"+jobName+"/api/json");

                response = webResource.header("Authorization", "Basic " + auth)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .get(ClientResponse.class);

                if (response.getStatus() != 200) {
                    if (response.getStatus() == 500) {
                        LOGGER.debug("Failed : HTTP error code : "
                                + response.getStatus());
                        LOGGER.debug(jenkinsURL);
                    }
                    LOGGER.debug("Error");
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }

                String jobDetails = response.getEntity(String.class);
                JSONObject jobDetailsObject = new JSONObject(jobDetails);

                String job_name = jobDetailsObject.get("name").toString();
                String job_url = jobDetailsObject.get("url").toString();
                String job_color = jobDetailsObject.get("color").toString();
                String last_success_no = null;
                String last_success_url = null;
                String last_failure_no = null;
                String last_failure_url = null;
                if(!(jobDetailsObject.get("lastSuccessfulBuild").toString().equals("null"))){
                    last_success_no = jobDetailsObject.getJSONObject("lastSuccessfulBuild").get("number").toString();
                    last_success_url = jobDetailsObject.getJSONObject("lastSuccessfulBuild").get("url").toString();
                }
                if(!(jobDetailsObject.get("lastFailedBuild").toString().equals("null"))){
                    last_failure_no = jobDetailsObject.getJSONObject("lastFailedBuild").get("number").toString();
                    last_failure_url = jobDetailsObject.getJSONObject("lastFailedBuild").get("url").toString();
                }

                db.writeData(job_name, job_url, job_color,
                        last_success_no, last_success_url,
                        last_failure_no, last_failure_url);

                LOGGER.info("*******************************************************");
                LOGGER.info("Name : " + job_name);
                LOGGER.info("URL : " + job_url);
                LOGGER.debug("Color : " + job_color);
                LOGGER.debug("LSN : " + last_success_no);
                LOGGER.debug("LSU : " + last_success_url);
                LOGGER.debug("LFN : " + last_failure_no);
                LOGGER.debug("LFU : " + last_failure_url);
                LOGGER.info("*******************************************************");
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return output;
    }
}

