package com.wso2.edash.patchcollector.invoke;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.File;


/**
 * Created by yashira on 7/1/14.
 */
public class Connector {

    static {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("192.168.66.11")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    public Connector(){
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir+"/properties/client-truststore.jks");
        /*File file = new File("/home/yashira/IdeaProjects/PatchCollector/" +
                "src/main/resources/client-truststore.jks");*/

        if(file.exists()){
            System.setProperty("javax.net.ssl.trustStore", file.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        }
        else{
            throw new RuntimeException("Property files cannot be resolved.");
        }

    }

    public String getData(String url){
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse responce = webResource.type("application/json")
                .accept("application/json").get(ClientResponse.class);

        try{
            if(responce.getStatus() != 200){
                throw new RuntimeException("Request cannot be send");
            }
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        return responce.getEntity(String.class);
    }
}
