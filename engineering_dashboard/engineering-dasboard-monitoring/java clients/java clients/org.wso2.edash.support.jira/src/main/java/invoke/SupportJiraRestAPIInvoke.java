package invoke;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import publish.PrintLog;
import schedule.SupportJiraRestAPICall;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class SupportJiraRestAPIInvoke {
	Properties prop;
	ClientResponse response;
	String output;
	String supportJiraUserName;
	String supportJiraUserPassword;
	PrintLog log = new PrintLog();

	public SupportJiraRestAPIInvoke() {
		prop = new Properties();

	}

	public String invoke(String Url) {

		try {
			prop.load(SupportJiraRestAPICall.class.getClassLoader()
					.getResourceAsStream("constants.properties"));
			supportJiraUserName = prop.getProperty("SUPPORT_JIRA_USERNAME");
			supportJiraUserPassword = prop.getProperty("SUPPORT_JIRA_PASSWORD");

			Client client = Client.create();

			String auth = new String(Base64.encode(supportJiraUserName + ":"
					+ supportJiraUserPassword));
			WebResource webResource = client.resource(Url);

			response = webResource.header("Authorization", "Basic " + auth)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.get(ClientResponse.class);

			if (response.getStatus() != 200) {
				if (response.getStatus() == 500) {
					SupportJiraRestAPICall restCall = new SupportJiraRestAPICall();
					String[] lastUpdatedDetails = restCall
							.getLastUpadatedDetails();
					int lastUpdatedUserNum = Integer
							.parseInt(lastUpdatedDetails[2]) + 1;
					restCall.updateDatabase(lastUpdatedDetails[0],
							lastUpdatedDetails[1], lastUpdatedUserNum);
					System.out.println("Failed : HTTP error code : "
							+ response.getStatus());
					System.out.println(Url);
					restCall.getIssues();

				}

				System.out.println("Failed : HTTP error code : "
						+ response.getStatus());
				log.write("Failed : HTTP error code : " + response.getStatus());
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			output = response.getEntity(String.class);

			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		output = response.getEntity(String.class);

		return output;

	}

}
