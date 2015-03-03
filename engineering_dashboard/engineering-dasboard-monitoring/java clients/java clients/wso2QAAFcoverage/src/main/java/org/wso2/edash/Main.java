package org.wso2.edash;

import com.google.gdata.util.ServiceException;
import org.wso2.edash.database.DataBaseConnection;
import org.wso2.edash.invoke.FileReader;
import org.wso2.edash.util.Product;

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
        Product product [] = fileReader.productlist();
        for (int i = 0;i<product.length;i++){

            dataBaseConnection.update(product[i]);
        }




//
//        String USERNAME = "wso2.edash@wso2.com";
//        String PASSWORD = "Edash-123";
//
//        Properties properties = new Properties();
//        properties.load(Main.class.getClassLoader()
//                .getResourceAsStream("constants.properties"));
//        System.out.println(properties.getProperty("databaseDriver"));
//        SpreadsheetService service =
//                new SpreadsheetService("MySpreadsheetIntegration-v1");
//        service.setUserCredentials(USERNAME, PASSWORD);
//        URL SPREADSHEET_FEED_URL = new URL(
//                "https://spreadsheets.google.com/feeds/list/0Ap3GxxuFiNkadDZlN2lISmhSOWNodWpvN04zSk5tVFE/od7/private/full");
//
//        // Make a request to the API and get all spreadsheets.
//
//        ListFeed feed = service.getFeed(SPREADSHEET_FEED_URL,  ListFeed.class);
//
//        List<ListEntry> spreadsheets = feed.getEntries();
//          int i = 0;
//        // Iterate through all of the spreadsheets returned
//        for ( ListEntry spreadsheet : spreadsheets) {
//             String a =  spreadsheet.getPlainTextContent();
//            String [] b = a.split(",");
//            System.out.println(b[0]);
//            // Print the title of this spreadsheet to the screen
//            System.out.println(spreadsheet.getTitle().getPlainText()+"   "+spreadsheet.getPlainTextContent());
//        i++;
//        }
//        System.out.println(i);
    }


}
