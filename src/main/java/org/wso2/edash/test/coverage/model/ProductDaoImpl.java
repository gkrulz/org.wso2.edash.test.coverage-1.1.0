package org.wso2.edash.test.coverage.model;

import org.apache.log4j.Logger;
import org.wso2.edash.test.coverage.dao.ProductDao;
import org.wso2.edash.test.coverage.database.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Product DAO implementation class
 * Contains methods to delete data and insert data to database
 */
public class ProductDaoImpl implements ProductDao {
	private static final Logger log = Logger.getLogger(ProductDaoImpl.class);

	/**
	 * Method to delete the data tables
	 *
	 * @throws SQLException
	 */
	public void deleteDataFromTable() throws SQLException, IOException {
		Connection connection = DatabaseManager.getInstance().getConnection();
		PreparedStatement preparedStatement = null;
		String sql = "DELETE FROM product_test_coverage;";

		if (connection != null) {
			preparedStatement = connection.prepareStatement(sql);
			if (preparedStatement != null) {
				preparedStatement.execute();
				if (!connection.getAutoCommit()) {
					connection.commit();
				}
				preparedStatement.close();
			}
		}
	}

	/**
	 * Method to insert data into the database table
	 *
	 * @param product
	 * @throws SQLException
	 */
	public void insertProduct(Product product) throws SQLException, IOException {
		Connection connection = DatabaseManager.getInstance().getConnection();
		PreparedStatement preparedStatement = null;
		String sql =
				"INSERT INTO product_test_coverage (product_name,version,loccoveragemaximum,loccoveragecurrentmonth,classcoveragemaximum,classcoveragecurrentmonth) VALUES (?,?,?,?,?,?);";

		if (connection != null) {
			preparedStatement = connection.prepareStatement(sql);
			if (preparedStatement != null) {
				preparedStatement.setString(1, product.getName());
				preparedStatement.setString(2, product.getVersion());

				preparedStatement.setString(3, product.getLoccoveragemaximum());
				preparedStatement.setString(4, product.getLoccoveragecurrentmonth());

				preparedStatement.setString(5, product.getClasscoveragemaximum());
				preparedStatement.setString(6, product.getClasscoveragecurrentmonth());

				preparedStatement.executeUpdate();
				log.debug("---------- DB-UPDATED " + product.getName() + " ----------");
				if (!connection.getAutoCommit()) {
					connection.commit();
				}
				preparedStatement.close();
			}
		}
	}
}
