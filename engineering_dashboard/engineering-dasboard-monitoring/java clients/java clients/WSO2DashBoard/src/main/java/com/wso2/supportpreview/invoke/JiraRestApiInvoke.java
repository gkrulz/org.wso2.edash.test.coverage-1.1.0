package com.wso2.supportpreview.invoke;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by yashira on 5/5/14.
 */
public class JiraRestApiInvoke {

    private Properties properties = new Properties();
    private String output;
    private String supportJiraPassword;
    private String supportJiraUsername;
    private String userDir = "";

    static {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("support.wso2.com")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    public JiraRestApiInvoke(){
        userDir = System.getProperty("user.dir");
    }

    public String invoke(String url){
        try{
            //properties.load(new FileInputStream("/home/yashira/WSO2DashBoard/src/main/resources/resolveCounter_constants.properties"));
            properties.load(new FileInputStream(userDir+"/properties/resolveCounter_constants.properties"));

            supportJiraPassword = properties.getProperty("SUPPORT_JIRA_PASSWORD");
            supportJiraUsername = properties.getProperty("SUPPORT_JIRA_USERNAME");


            Client client = Client.create();

            String authentication = new String(Base64.encode(supportJiraUsername+":"+supportJiraPassword));
            WebResource webResource = client.resource(url);
            System.out.println("URL is "+url);
            //ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            ClientResponse response = webResource.header("Authorization", "Basic " +authentication)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if(response.getStatus() != 200){
              throw new RuntimeException("Failed HTTP Error Code : "+response.getStatus());
            }

        output = response.getEntity(String.class);
        }catch (IOException ioex){
            ioex.printStackTrace();
        }
        return output;
    }
}
