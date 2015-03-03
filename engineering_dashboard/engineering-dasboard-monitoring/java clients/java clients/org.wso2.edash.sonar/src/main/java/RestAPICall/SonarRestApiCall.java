package RestAPICall;

import invoke.APIInvoke;

import java.io.IOException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.Database;

public class SonarRestApiCall {

	Properties prop;
	Database database;

	public SonarRestApiCall() {
		// TODO Auto-generated constructor stub
		database = new Database();
		database.connectToDatabase();
		database.deleteTable();

		prop = new Properties();
		loadProperties();
	}

	public void loadProperties() {

		try {
			prop.load(SonarRestApiCall.class.getClassLoader()
					.getResourceAsStream("constants.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getSonarDetails() {
		APIInvoke apiInvoke = new APIInvoke();
		String out = prop.getProperty("SONAR_DETAILS");

		String restOut = apiInvoke.invoke(out);

		try {
			JSONArray sonarArray = new JSONArray(restOut);
			// System.out.println(sonarArray);
			for (int i = 0; i < sonarArray.length(); i++) {
				JSONObject jsonObject = sonarArray.getJSONObject(i);
				try {
					String branch = jsonObject.get("branch").toString()
							.toLowerCase();

					System.out.println(branch + "\n");
					JSONArray sonarCoverage = jsonObject.getJSONArray("msr");
					String coverage = sonarCoverage.getJSONObject(0).get("val")
							.toString();
					System.out.println(coverage);
					database.updateDB(branch, coverage);
				} catch (JSONException e) {
					// e.printStackTrace();
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
