package org.wso2.edash.test.coverage.model;

import org.apache.log4j.Logger;
import org.wso2.edash.test.coverage.dao.ProductDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Padmaka Wijayagoonawardena on 4/20/15.
 * Email - padmakaj@wso2.com
 */
public class ProductDaoImpl implements ProductDao{
    private static final Logger LOGGER = Logger.getLogger(ProductDaoImpl.class);

    /**
     * Method to delete the data tables
     * @throws SQLException
     */
    @Override
    public void deleteDataFromTable(Connection con) throws SQLException {
        PreparedStatement preparedStatement = null;
        String sql = "DELETE FROM product_test_coverage;";

        if(con != null){
            preparedStatement = con.prepareStatement(sql);
            if(preparedStatement != null){
                preparedStatement.execute();
                if (!con.getAutoCommit()) {
                    con.commit();
                }
                preparedStatement.close();
            }
        }
    }

    /**
     * Method to insert data into the database table
     * @param product
     * @throws SQLException
     */
    @Override
    public void insertProduct(Product product,Connection con) throws SQLException{
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO product_test_coverage (product_name,version,loccoveragemaximum,loccoveragecurrentmonth,classcoveragemaximum,classcoveragecurrentmonth) VALUES (?,?,?,?,?,?);";

        if(con != null){
            preparedStatement = con.prepareStatement(sql);
            if(preparedStatement != null){
                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getVersion());

                preparedStatement.setString(3, product.getLoccoveragemaximum());
                preparedStatement.setString(4, product.getLoccoveragecurrentmonth());

                preparedStatement.setString(5, product.getClasscoveragemaximum());
                preparedStatement.setString(6, product.getClasscoveragecurrentmonth());

                preparedStatement.executeUpdate();
                LOGGER.debug("---------- DB-UPDATED "+product.getName()+" ----------");
                if (!con.getAutoCommit()) {
                    con.commit();
                }
                preparedStatement.close();
            }
        }
    }
}
