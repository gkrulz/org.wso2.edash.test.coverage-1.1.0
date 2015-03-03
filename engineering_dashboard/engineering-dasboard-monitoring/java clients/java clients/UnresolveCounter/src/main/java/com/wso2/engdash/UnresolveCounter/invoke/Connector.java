package com.wso2.engdash.UnresolveCounter.invoke;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by yashira on 8/7/14.
 */
public class Connector {

    private String output;
    private String userDir = "";
    private File file;

    static {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("192.168.66.25")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    public Connector(){
        this.userDir = System.getProperty("user.dir");
        try {
            //this.file = new File("/home/yashira/IdeaProjects/UnresolveCounter/src/main/resources/client-truststore.jks");
            this.file = new File(userDir+"/properties/client-truststore.jks");
            if (file.exists()) {
                System.setProperty("javax.net.ssl.trustStore", file.getAbsolutePath());
                System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
            } else {
                throw new FileNotFoundException("client-truststore.jks file cannot be located");
            }
        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public String connenctNonSecureLine(String url){

        Client client = Client.create();

        WebResource webResource = client.resource(url);
        ClientResponse response = webResource
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        if(response.getStatus() != 200){
            throw new RuntimeException("Failed HTTP Error Code : "+response.getStatus());
        }

        output =  response.getEntity(String.class);


        return output;
    }

    public String getJIRADetails(String url,String username,String password){

        Client client = Client.create();

        String authentication = new String(Base64.encode(username + ":" + password));
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.header("Authorization", "Basic " +authentication)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        if(response.getStatus() != 200){
            throw new RuntimeException("Failed HTTP Error Code : "+response.getStatus());
        }

        output =  response.getEntity(String.class);


        return output;
    }
}
