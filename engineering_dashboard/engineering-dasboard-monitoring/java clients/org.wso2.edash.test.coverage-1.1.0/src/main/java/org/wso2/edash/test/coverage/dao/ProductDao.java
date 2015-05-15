package org.wso2.edash.test.coverage.dao;

import org.wso2.edash.test.coverage.model.Product;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Product DAO interface
 */
public interface ProductDao {
	public void deleteDataFromTable() throws SQLException, IOException;

	public void insertProduct(Product product) throws SQLException, IOException;
}
