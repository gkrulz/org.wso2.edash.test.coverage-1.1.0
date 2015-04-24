package org.wso2.edash.test.coverage;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.wso2.edash.test.coverage.database.DatabaseManager;
import org.wso2.edash.test.coverage.invoke.FileReader;
import org.wso2.edash.test.coverage.model.Product;
import org.wso2.edash.test.coverage.model.ProductDaoImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/***
 * Main Class
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(FileReader.class.getName());

    private Main(){

    }

    /***
     * Main method
     * @param args
     */
    public static void main  (String []args) {
        DOMConfigurator.configure(System.getProperty("user.dir") + "/properties/log4j.xml");
        ProductDaoImpl productDao = new ProductDaoImpl();
        FileReader fileReader = new FileReader();
        Connection connection = null;
        List<Product> products = null;

        try {
            connection = DatabaseManager.getInstance().getConnection();
            productDao.deleteDataFromTable(connection);
            products = fileReader.getProductlist();

            for (int i = 0; i < products.size(); i++) {
                productDao.insertProduct(products.get(i), connection);
            }
        }catch (SQLException e){
            LOGGER.error(e);
        } finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.info("---System updated---");
    }
}
