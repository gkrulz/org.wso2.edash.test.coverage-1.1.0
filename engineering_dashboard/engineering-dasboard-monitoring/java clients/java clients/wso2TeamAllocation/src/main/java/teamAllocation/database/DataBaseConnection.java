package teamAllocation.database;

import teamAllocation.util.Components;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


public final class DataBaseConnection {
    private static Connection connection;
    private static PreparedStatement insertStatement;
    private PreparedStatement preparedStatement;
    Properties properties;



    public void deleteTable() throws IOException {
        properties = new Properties();
        String sql = "DELETE FROM TEAM_ALLOCATION;";
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

    public void update(Components components) {
        String sql = "INSERT INTO TEAM_ALLOCATION (productName,allocationAvg) VALUES (?,?);";

        // publisher.dataPublish(userIssues);

        try {
            Class.forName(properties.getProperty("databaseDriver")).newInstance();
            String db_url = properties.getProperty("dbUrl");
            String db_username = properties.getProperty("dbUserName");
            String db_password = properties.getProperty("dbPwd");
            Connection con = DriverManager.getConnection(db_url, db_username,
                    db_password);


            preparedStatement = con.prepareStatement(sql);
            System.out.println(components.getProductName()+"   "+ components.getTeamAllocation());
            preparedStatement.setString(1, components.getProductName());
            preparedStatement.setString(2, components.getTeamAllocation());





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