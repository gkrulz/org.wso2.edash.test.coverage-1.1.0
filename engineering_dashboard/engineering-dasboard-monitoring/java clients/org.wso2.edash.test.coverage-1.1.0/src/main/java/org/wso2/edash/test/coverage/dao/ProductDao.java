package org.wso2.edash.test.coverage.dao;

import org.wso2.edash.test.coverage.model.Product;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Padmaka Wijayagoonawardena on 4/20/15.
 * Email - padmakaj@wso2.com
 */
public interface ProductDao {
    public void deleteDataFromTable(Connection connection) throws SQLException;
    public void insertProduct(Product product,Connection connection) throws SQLException;
}
