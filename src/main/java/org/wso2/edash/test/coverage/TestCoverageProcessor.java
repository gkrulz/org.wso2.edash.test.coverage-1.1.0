package org.wso2.edash.test.coverage;

import com.google.gdata.util.ServiceException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.wso2.edash.test.coverage.dao.ProductDao;
import org.wso2.edash.test.coverage.invoke.TestCoverageGoogleDocReader;
import org.wso2.edash.test.coverage.model.Product;
import org.wso2.edash.test.coverage.model.ProductDaoImpl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

/**
 * Main Class
 */
public class TestCoverageProcessor {
	private static final Logger log = Logger.getLogger(TestCoverageGoogleDocReader.class.getName());
	private ProductDao productDao;

	/**
	 * private constructor
	 */
	private TestCoverageProcessor() {
		productDao = new ProductDaoImpl();
	}

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.dir") + "/properties/log4j.xml");
		TestCoverageProcessor main = new TestCoverageProcessor();
		TestCoverageGoogleDocReader testCoverageGoogleDocReader = null;
		List<Product> products = null;

		try {
			testCoverageGoogleDocReader = new TestCoverageGoogleDocReader();
		} catch (IOException | GeneralSecurityException e) {
			log.error("Failed to Authenticate to google spreadsheets", e);
		}

		//get the product test coverage data
		try {
			products = testCoverageGoogleDocReader.getProductlist();
		} catch (IOException | ServiceException e) {
			log.error("Failed to get test coverage data", e);
		}

		//Delete data from the table
		try {
			main.productDao.deleteDataFromTable();
		} catch (SQLException | IOException e) {
			log.error("Failed to delete data from the table", e);
		}

		//insert data into database
		try {
			for (Product product : products) {
				main.productDao.insertProduct(product);
			}
		} catch (SQLException | IOException e) {
			log.error("Failed to insert into table", e);
		}
		log.info("---System updated---");
	}
}