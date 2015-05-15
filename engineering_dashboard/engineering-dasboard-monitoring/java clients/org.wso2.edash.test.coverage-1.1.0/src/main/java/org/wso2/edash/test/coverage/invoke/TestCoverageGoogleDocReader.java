package org.wso2.edash.test.coverage.invoke;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.apache.log4j.Logger;
import org.wso2.edash.test.coverage.model.Product;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
	public TestCoverageGoogleDocReader() throws IOException, AuthenticationException {
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
	private void authenticate() throws AuthenticationException {
		//get properties from test_coverage_constants.properties file
		String username = properties.getProperty("USERNAME");
		String password = properties.getProperty("PASSWORD");

		service = new SpreadsheetService("MySpreadsheetIntegration-v1");
		service.setUserCredentials(username, password);
	}

}