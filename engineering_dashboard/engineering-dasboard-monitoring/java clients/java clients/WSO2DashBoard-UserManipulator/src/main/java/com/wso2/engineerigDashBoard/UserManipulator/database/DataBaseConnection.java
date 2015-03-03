package com.wso2.engineerigDashBoard.UserManipulator.database;

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

    public DataBaseConnection(Properties properties){
        try {
            Class.forName(properties.getProperty("databaseDriver")).newInstance();
            DataBaseConnection.connection = DriverManager.getConnection(properties.getProperty("dbUrl"),
                    properties.getProperty("dbUserName"), properties.getProperty("dbPwd"));
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void shutDownConnection(){
        if (DataBaseConnection.insertStatement != null){
            try {
                DataBaseConnection.insertStatement.close();
            }catch (SQLException ex){
                System.out.println("Cannot shutdown the connection");
            }
        }
        if(DataBaseConnection.connection != null){
            try {
                DataBaseConnection.connection.close();
            }catch (SQLException ex){
                System.out.println("Cannot shutdown the connection");
            }
        }
    }

    public Connection getConnection(){
        return DataBaseConnection.connection;
    }

    public void initStatement(String query){
        try {
            DataBaseConnection.insertStatement = DataBaseConnection.connection.prepareStatement(query);
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void setStatement(String userEmail,String productName,String tgName,String type){
        try {
            DataBaseConnection.insertStatement.setString(1,userEmail);
            DataBaseConnection.insertStatement.setString(2,productName);
            DataBaseConnection.insertStatement.setString(3,tgName);
            DataBaseConnection.insertStatement.setString(4,type);

            DataBaseConnection.insertStatement.addBatch();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }

    }

    public void executeBatch(){
        try {
            DataBaseConnection.insertStatement.executeBatch();
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
}
