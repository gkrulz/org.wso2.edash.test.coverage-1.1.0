package org.wso2.edash.database;

import org.wso2.edash.util.Product;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by yashira on 6/13/14.
 */
public final class DataBaseConnection {
    private static Connection connection;
    private static PreparedStatement insertStatement;
    private PreparedStatement preparedStatement;
    Properties properties;



    public void deleteTable() throws IOException {
        properties = new Properties();
        String sql = "DELETE FROM PRODUCT_TEST_COVERAGE;";
        properties.load(DataBaseConnection.class.getClassLoader()
                .getResourceAsStream("constants.properties"));

        // publisher.dataPublish(userIssues);

        try {
            Class.forName(properties.getProperty("databaseDriver")).newInstance();
            String db_url = properties.getProperty("dbUrl");
            String db_username = properties.getProperty("dbUserName");
            String db_password = properties.getProperty("dbPwd");
            Connection con = DriverManager.getConnection(db_url, db_username,
                    db_password);

            preparedStatement = con.prepareStatement(sql);

            preparedStatement.execute();
            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void update(Product products) {
        String sql = "INSERT INTO PRODUCT_TEST_COVERAGE (product_name,version,loccoveragemaximum,loccoveragecurrentmonth,classcoveragemaximum,classcoveragecurrentmonth) VALUES (?,?,?,?,?,?);";

        // publisher.dataPublish(userIssues);

        try {
            Class.forName(properties.getProperty("databaseDriver")).newInstance();
            String db_url = properties.getProperty("dbUrl");
            String db_username = properties.getProperty("dbUserName");
            String db_password = properties.getProperty("dbPwd");
            Connection con = DriverManager.getConnection(db_url, db_username,
                    db_password);


            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, products.getName());
            preparedStatement.setString(2, products.getVersion());

            preparedStatement.setString(3, products.getLoccoveragemaximum());
            preparedStatement.setString(4, products.getLoccoveragecurrentmonth());

            preparedStatement.setString(5, products.getClasscoveragemaximum());
            preparedStatement.setString(6, products.getClasscoveragecurrentmonth());



            preparedStatement.executeUpdate();
            System.out
                    .println(".......................DB-UPDATED...............................");
            if (!con.getAutoCommit()) {
                con.commit();
            }
            con.close();

        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
