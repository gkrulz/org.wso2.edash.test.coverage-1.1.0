package com.wso2.engdashboard.edb.hive.ResolveCountHelper;

import com.wso2.engdashboard.edb.hive.ResolveCountHelper.utils.DataAccessObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.hive.extension.AbstractHiveAnalyzer;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Asma on 2/26/15.
 */
public class EDBHiveIssueAnalyzer extends AbstractHiveAnalyzer {
    private String databaseTableName = "LastUpdatedJIRADailyDetails";
    private String tablePropertyName = "last_issue_date";
    private String datePattern = "yyyy-MM-dd HH:00:00";
    private static Log log = LogFactory.getLog(EDBHiveIssueAnalyzer.class);

    @Override
    public final void execute(){
        try {
            String tableName = this.databaseTableName;
            String lastDailyTimestampStr = DataAccessObject.getInstance().UpdateLastTimestamp(tableName);
            Long lastDailyTimestampSecs = Timestamp.valueOf(lastDailyTimestampStr).getTime() / 1000;

            DateFormat formatter = new SimpleDateFormat(this.datePattern);
            String currentTsStr = formatter.format(new Date().getTime());
            Long currentTsSecs = Timestamp.valueOf(currentTsStr).getTime() / 1000;


            setProperty(this.tablePropertyName, lastDailyTimestampSecs.toString());

        } catch (Exception e) {
            System.out.println("An error occurred while setting date range for daily usage analysis. "+ e.getMessage());
            System.exit(1);
        }

    }
}
