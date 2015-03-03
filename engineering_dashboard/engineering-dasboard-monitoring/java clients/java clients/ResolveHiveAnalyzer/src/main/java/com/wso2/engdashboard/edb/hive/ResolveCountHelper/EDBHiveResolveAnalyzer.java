package com.wso2.engdashboard.edb.hive.ResolveCountHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.hive.extension.AbstractHiveAnalyzer;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wso2.engdashboard.edb.hive.ResolveCountHelper.utils.*;

/**
 * Created by yashira on 7/9/14.
 */
public class EDBHiveResolveAnalyzer extends AbstractHiveAnalyzer {

    private String databaseTableName = "LastUpdatedDailyDetailsResolve";
    private String tablePropertyName = "last_resolved_date";
    private String datePattern = "yyyy-MM-dd HH:00:00";
    private static Log log = LogFactory.getLog(EDBHiveResolveAnalyzer.class);

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
