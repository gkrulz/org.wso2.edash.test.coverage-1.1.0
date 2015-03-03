package invoke;

import java.util.Properties;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class APIInvoke {

	Properties prop;
	ClientResponse response;
	String output;
	String UserName;
	String Password;

	public APIInvoke() {

		prop = new Properties();

	}

	public String invoke(String Url) {
		try {

			Client client = Client.create();

			WebResource webResource = client.resource(Url);

			ClientResponse response = webResource.accept("application/json")
					.get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			output = response.getEntity(String.class);

			return output;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}

}
