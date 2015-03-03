package teamAllocation;

import com.google.gdata.util.ServiceException;
import teamAllocation.database.DataBaseConnection;
import teamAllocation.invoke.FileReader;
import teamAllocation.util.Components;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;


public class Main {

    public static void main  (String []args) throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException, SQLException {

        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        dataBaseConnection.deleteTable();
        FileReader fileReader = new FileReader();
        Components product [] = fileReader.productlist();
        for (int i = 0;i<fileReader.getCount();i++){

            dataBaseConnection.update(product[i]);
        }





//        String USERNAME = "wso2.edash@wso2.com";
//        String PASSWORD = "iqxlzlwtrbjswcbq";
//
//
//
//        SpreadsheetService service =
//                new SpreadsheetService("MySpreadsheetIntegration-v1");
//        service.setUserCredentials(USERNAME, PASSWORD);
//        URL SPREADSHEET_FEED_URL = new URL(
//                "https://spreadsheets.google.com/feeds/list/1c95Qaa_lRViBsu6XwWoSQYS5iu9U4rcV_1xzupRXN8Y/od6/private/basic");
//
//        // Make a request to the API and get all spreadsheets.
//
//        ListFeed feed = service.getFeed(SPREADSHEET_FEED_URL,  ListFeed.class);
//        CellFeed cellfeed = service.getFeed(SPREADSHEET_FEED_URL,  CellFeed.class);
//        List<ListEntry> spreadsheets = feed.getEntries();
//          int i = 0;
//        // Iterate through all of the spreadsheets returned
//        for ( ListEntry spreadsheet : spreadsheets) {
//
//            // Print the title of this spreadsheet to the screen
//
//            Date date = new Date();
//
//            System.out.println(spreadsheet.getTitle().getPlainText()+"       "+spreadsheet.getPlainTextContent());
//        i++;
//        }
//        System.out.println(i);
    }


}
