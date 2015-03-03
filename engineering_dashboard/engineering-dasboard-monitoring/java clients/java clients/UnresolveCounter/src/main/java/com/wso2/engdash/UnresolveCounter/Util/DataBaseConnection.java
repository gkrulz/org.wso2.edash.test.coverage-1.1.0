package com.wso2.engdash.UnresolveCounter.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by yashira on 8/8/14.
 */
public class DataBaseConnection {

    private String userName;
    private String password;
    private String dbDriver;
    private String dbUrl;
    private File file;
    private Properties properties = new Properties();
    private Connection connection;

    public DataBaseConnection(){
        String userDir = System.getProperty("user.dir");
        try {
            file = new File(userDir+"/properties/dbProperties.properties");
           //file = new File("/home/yashira/IdeaProjects/UnresolveCounter/src/main/resources/dbProperties.properties");
            properties.load(new FileInputStream(file));

            this.userName = this.properties.getProperty("dbUserName");
            this.password = this.properties.getProperty("dbPwd");
            this.dbDriver = this.properties.getProperty("databaseDriver");
            this.dbUrl = this.properties.getProperty("dbUrl");

            Class.forName(dbDriver).newInstance();

            this.connection = DriverManager.getConnection(this.dbUrl,this.userName,this.password);

        }catch (FileNotFoundException fne){
            System.out.println("Cannot find the dbProperties.properties on specified path.");
        }
        catch (IOException io){

        }
        catch (Exception ex){

        }
    }

    public boolean insertQuery(String query){
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return true;
    }

}
