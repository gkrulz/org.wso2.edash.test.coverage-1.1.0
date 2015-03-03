package com.wso2.engdashboard.edb.hive.ResolveCountHelper.utils;


import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper class to retrieve relevant timestamps from relational database tables.
 */

public class DataAccessObject {


    //private static Log log = LogFactory.getLog(DataAccessObject.class);


    private static DataAccessObject usageDataAccessObj = null;

    private DataAccessObject() {
    }

    public static DataAccessObject getInstance() throws Exception {
        if (usageDataAccessObj == null) {
            usageDataAccessObj = new DataAccessObject();
        }

        return usageDataAccessObj;
    }


    public String UpdateLastTimestamp(String tableName) throws SQLException {

        Timestamp lastSummaryTs = null;
        Connection connection = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:00");


        try {
            connection = DBUtil.getConnection();

            Statement stmt = connection.createStatement();

            //String sqlDrop = "DROP TABLE "+tableName;

            //stmt.executeUpdate(sqlDrop);

            String sql = "CREATE TABLE IF NOT EXISTS "+tableName +
                    "(last_updated_date TIMESTAMP not NULL " +
                    ")";

            stmt.executeUpdate(sql);


            sql = "SELECT last_updated_date from "+tableName;


            ResultSet rs = stmt.executeQuery(sql);

            Date currentDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);

           // String   currentTs   =  dateFormat.format(cal.getTime());

            Timestamp currentTs = new Timestamp(cal.getTime().getTime());

            if (rs.next()) {

                lastSummaryTs = rs.getTimestamp("last_updated_date");
                String currentSql = "UPDATE "+tableName+" SET last_updated_date = ?";
                PreparedStatement ps1 = connection.prepareStatement(currentSql);
                ps1.setTimestamp(1, currentTs);
                ps1.execute();

            } else {

                   if(!tableName.contains("JIRA")) {
                         lastSummaryTs = new Timestamp(2012-1900,0,31,0,0,0,0);
                   }
                    else{
                       lastSummaryTs = new Timestamp(2006-1900,10,9,0,0,0,0);
                   }
                String currentSql = "INSERT INTO "+tableName+" (last_updated_date) VALUES(?)";
                PreparedStatement ps1 = connection.prepareStatement(currentSql);
                ps1.setTimestamp(1, currentTs);
                ps1.execute();
            }



        } catch (SQLException e) {
            System.out.println("Error occurred while trying to get and update the last hourly timestamp. " + e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        return lastSummaryTs.toString();
    }


}
