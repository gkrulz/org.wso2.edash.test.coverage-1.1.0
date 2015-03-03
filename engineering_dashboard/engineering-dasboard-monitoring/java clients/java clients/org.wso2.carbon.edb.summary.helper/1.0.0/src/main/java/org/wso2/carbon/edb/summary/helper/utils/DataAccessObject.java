package org.wso2.carbon.edb.summary.helper.utils;


import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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


    public String UpdateLastDailyTimestamp() throws SQLException {

        Timestamp lastSummaryTs = null;
        Connection connection = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:00");


        try {
            connection = DBUtil.getConnection();

            Statement stmt = connection.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS LastUpdatedDailyDetails " +
                    "(last_updated_date TIMESTAMP not NULL " +
                    ")";

            stmt.executeUpdate(sql);


            sql = "SELECT last_updated_date from LastUpdatedDailyDetails";


            ResultSet rs = stmt.executeQuery(sql);


            if (rs.next()) {

                lastSummaryTs = rs.getTimestamp("last_updated_date");
            } else {


                lastSummaryTs = new Timestamp(2012 - 1900, 0, 31, 0, 0, 0, 0);
            }


            Date currentDate = new Date();
            Timestamp currentTs = new Timestamp(currentDate.getTime());


            String currentSql = "INSERT INTO LastUpdatedDailyDetails (last_updated_date) VALUES(?)";
            PreparedStatement ps1 = connection.prepareStatement(currentSql);
            ps1.setTimestamp(1, currentTs);
            ps1.execute();

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
