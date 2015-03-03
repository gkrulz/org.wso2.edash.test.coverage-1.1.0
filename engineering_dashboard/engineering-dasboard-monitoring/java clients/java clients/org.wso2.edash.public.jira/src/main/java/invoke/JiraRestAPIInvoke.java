package invoke;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;



import javax.ws.rs.core.MediaType;

public class JiraRestAPIInvoke {

	public JiraRestAPIInvoke() {

	}

	public String invoke(String Url)  {


    Client client = Client.create();


    WebResource webResource = client
            .resource(Url);

    ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

    if (response.getStatus() != 200) {
        throw new RuntimeException("Failed : HTTP error code : "
                + response.getStatus());
    }

    String output = response.getEntity(String.class);

		return output;

	}

}

