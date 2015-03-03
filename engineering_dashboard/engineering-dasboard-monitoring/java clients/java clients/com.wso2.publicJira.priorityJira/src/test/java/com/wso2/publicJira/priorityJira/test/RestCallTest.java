package com.wso2.publicJira.priorityJira.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

/**
 * Created by Padmaka Wijayagoonawardena on 10/14/14.
 * Email - padmakaj@wso2.com
 */
public class RestCallTest {

    public static void main(String[] args) {
        String output;

        RestCallTest restTest = new RestCallTest();
        output = restTest.invoke("https://wso2.org/jira/rest/api/2/search?jql=project%20%3D%20APPFAC%20AND%20resolution%20%3D%20Unresolved%20AND%20priority%20%3D%20lowest&maxResults=0");
        System.out.println(output);
    }

    public String invoke(String Url)  {


        Client client = Client.create();


        WebResource webResource = client.resource(Url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        return output;

    }
}
