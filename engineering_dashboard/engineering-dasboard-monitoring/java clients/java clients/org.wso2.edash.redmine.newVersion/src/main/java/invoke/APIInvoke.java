package invoke;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class APIInvoke {

    Properties prop;
    ClientResponse response;
    String output;
    String UserName;
    String Password;


    public APIInvoke() {

        prop = new Properties();

        try {
            prop.load(APIInvoke.class.getClassLoader().getResourceAsStream("redmine-constants-url.properties"));
            UserName = prop.getProperty("REDMINE_USERNAME");
            Password = prop.getProperty("REDMINE_PASSWORD");

        } catch (IOException e) {
            //LOGGER.error("ERROR WHILE LOADING PROPERTIES");
            e.printStackTrace();
        }
    }

    public String invoke(String Url) {
        try {

            Client client = Client.create();

            String auth = new String(Base64.encode(UserName + ":" + Password));
            WebResource webResource = client.resource(Url);

            response = webResource.header("Authorization", "Basic " + auth)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {

                System.out.println("Failed : HTTP error code : "
                        + response.getStatus());

                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            output = response.getEntity(String.class);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        output = response.getEntity(String.class);
        return output;
    }


}
