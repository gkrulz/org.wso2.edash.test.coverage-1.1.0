package com.wso2.edash.patchcollector.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Created by yashira on 7/2/14.
 */
public class DataBaseConnector {

    private Connection connection;
    private Properties properties;
    private String url;
    private String userName;
    private String password;
    private String dbDriver;

    public DataBaseConnector(){
        this.properties = new Properties();
        this.loadProperties();
        this.connection = this.getConnection();
        if(this.connection == null){
            throw new NullPointerException("The connection returned is null");
        }
    }
    public void loadProperties(){
        String filePath = System.getProperty("user.dir");
        //File file = new File(filePath+"/properties/dbProperties");
        File file = new File(filePath + "/properties/dbProperties");
        try {
            if (file.exists()) {
                try {
                    this.properties.load(new FileInputStream(file));

                    this.dbDriver = this.properties.getProperty("databaseDriver");
                    this.url = this.properties.getProperty("dbUrl");
                    this.userName = this.properties.getProperty("dbUserName");
                    this.password = this.properties.getProperty("dbPwd");
                } catch (IOException ex) {
                    System.out.println("IOException occurred while loading the Property file");
                    System.exit(1);
                }
            } else {
                throw new RuntimeException("File cannot be found in specified location.");
            }
        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    public Connection getConnection(){
        Connection con = null;
        try {
            Class.forName(this.dbDriver).newInstance();
            con = DriverManager.getConnection(this.url, this.userName, this.password);
        }catch (SQLException ex){
            System.out.println("Cannot establish the connection. "+ex.getMessage());
            System.exit(1);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return con;
    }
    public ResultSet getResults(String sql){
        ResultSet rs = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
        } catch (SQLException e) {
           System.out.println(e.getMessage());
           System.exit(1);
        }
        return rs;
    }
    public int executeQuery(String query){
        int i = 0;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            i = statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return i;
    }
    public void closeConnection(){
        if(this.connection != null){
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
