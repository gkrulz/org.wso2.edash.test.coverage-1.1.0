package org.wso2.carbon.edb.summary.helper;


import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.hive.extension.AbstractHiveAnalyzer;
import org.wso2.carbon.edb.summary.helper.utils.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;


public class EDBHiveDailyAnalyzerHelper extends AbstractHiveAnalyzer {

    private static Log log = LogFactory.getLog(EDBHiveDailyAnalyzerHelper.class);

    @Override
    public void execute() {
        try {
            String lastDailyTimestampStr = DataAccessObject.getInstance().UpdateLastDailyTimestamp();
            Long lastDailyTimestampSecs = Timestamp.valueOf(lastDailyTimestampStr).getTime() / 1000;

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            String currentTsStr = formatter.format(new Date().getTime());
            Long currentTsSecs = Timestamp.valueOf(currentTsStr).getTime() / 1000;

            setProperty("last_daily_ts", lastDailyTimestampSecs.toString());
           // setProperty("current_daily_ts", currentTsSecs.toString());
        } catch (Exception e) {
            log.error("An error occurred while setting date range for daily usage analysis. ", e);
        }


    }


}
