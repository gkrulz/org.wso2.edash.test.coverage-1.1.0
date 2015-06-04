package org.wso2.edash.test.coverage.invoke;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.ServiceException;
import org.apache.log4j.Logger;
import org.wso2.edash.test.coverage.model.Product;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Class to read the monthly test coverage doc
 */
public class TestCoverageGoogleDocReader {
	private static final Logger log = Logger.getLogger(TestCoverageGoogleDocReader.class.getName());
	private Properties properties;
	private SpreadsheetService service;

	/**
	 * Overridden constructor
	 */
	public TestCoverageGoogleDocReader() throws IOException, GeneralSecurityException {
		properties = new Properties();
		properties.load(TestCoverageGoogleDocReader.class.getClassLoader()
		                                .getResourceAsStream("test_coverage_constants.properties"));

		authenticate();
	}

	/**
	 * Read the spreadsheet and add the product information into product objects
	 *
	 * @return products
	 */
	public List<Product> getProductlist() throws IOException, ServiceException {
		String locCoverageMaximum;
		String locCoverageCurrentMonth;
		String classCoverageMax;
		String classCoverageCurrentMonth;

		List<Product> products = new ArrayList<Product>();

		URL spreadsheetFeedUrl = new URL(properties.getProperty("URL"));

		// Make a request to the API and get all spreadsheets.

		ListFeed feed = service.getFeed(spreadsheetFeedUrl, ListFeed.class);

		List<ListEntry> spreadsheets = feed.getEntries();
		// Iterate through all of the spreadsheets returned
		for (ListEntry spreadsheet : spreadsheets) {
			Product product = new Product();
			// Print the title of this spreadsheet to the screen
			log.info(spreadsheet.getTitle().getPlainText());
			String row = spreadsheet.getPlainTextContent();
			String[] split = row.split(",");
			product.setName(spreadsheet.getTitle().getPlainText().toString());
			product.setVersion(split[0].replace("version:", ""));

			locCoverageMaximum = split[1].replace("loccoveragemaximum:", "");
			locCoverageMaximum = locCoverageMaximum.replace("%", "");
			if (locCoverageMaximum.contains("NA")) {
				product.setLoccoveragemaximum(null);
			} else {
				product.setLoccoveragemaximum(locCoverageMaximum);
			}
			locCoverageCurrentMonth = split[2].replace("loccoveragecurrentmonth:", "");
			locCoverageCurrentMonth = locCoverageCurrentMonth.replace("%", "");
			if (locCoverageCurrentMonth.contains("NA")) {
				product.setLoccoveragecurrentmonth(null);
			} else {
				product.setLoccoveragecurrentmonth(locCoverageCurrentMonth);
			}
			classCoverageMax = split[3].replace("classcoveragemaximum:", "");
			classCoverageMax = classCoverageMax.replace("%", "");
			if (classCoverageMax.contains("NA")) {
				product.setClasscoveragemaximum(null);
			} else {
				product.setClasscoveragemaximum(classCoverageMax);
			}

			classCoverageCurrentMonth = split[4].replace("classcoveragecurrentmonth:", "");
			classCoverageCurrentMonth = classCoverageCurrentMonth.replace("%", "");
			if (classCoverageCurrentMonth.contains("NA")) {
				product.setClasscoveragecurrentmonth(null);
			} else {
				product.setClasscoveragecurrentmonth(classCoverageCurrentMonth);
			}
			products.add(product);
		}

		return products;
	}

	/**
	 * Method to authenticate with the google doc
	 */
	private void authenticate() throws GeneralSecurityException, IOException {
		//get properties from test_coverage_constants.properties file
		String username = properties.getProperty("USERNAME");

		String emailAddress = properties.getProperty("EMAIL_ADDRESS");
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(emailAddress)
				.setServiceAccountPrivateKeyFromP12File(new File(
						System.getProperty("user.dir") + "/properties/wso2_edash.p12"))
				.setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
				.setServiceAccountUser(username)
				.build();

		service = new SpreadsheetService("edashSpreadsheetService-v1");
		service.setOAuth2Credentials(credential);
	}
}