package hr;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ApiInvoke {

	public static String urlConnecter(String url, String type) {
		Client client = Client.create();

		if (type.contains("url")) {
			client.setFollowRedirects(true);
		}
		WebResource webResource = client.resource(url);

		ClientResponse response = webResource.accept(
				MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		String output = "";
		if (type.contains("url")) {
			output = response.toString();
		} else {
			output = response.getEntity(String.class);
		}
		return output;
	}

}
