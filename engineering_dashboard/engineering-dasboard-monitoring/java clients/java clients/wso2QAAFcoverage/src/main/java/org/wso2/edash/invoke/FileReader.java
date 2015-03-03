package org.wso2.edash.invoke;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.wso2.edash.util.Product;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;


public class FileReader {


    private Properties properties;
    private SpreadsheetService service ;

    public FileReader()  {

    properties = new Properties();
        try {
            properties.load(FileReader.class.getClassLoader()
                        .getResourceAsStream("constants.properties"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    public Product [] productlist(){
        authenticate();
        int proCount = productCount();


        Product product[] = new Product[proCount];

        try {

        URL SPREADSHEET_FEED_URL = new URL( properties.getProperty("URL"));

        // Make a request to the API and get all spreadsheets.

        ListFeed feed = service.getFeed(SPREADSHEET_FEED_URL,  ListFeed.class);


        List<ListEntry> spreadsheets = feed.getEntries();
        int i = 0;
        // Iterate through all of the spreadsheets returned
        for (ListEntry spreadsheet : spreadsheets) {

            // Print the title of this spreadsheet to the screen
            System.out.println(spreadsheet.getTitle().getPlainText()+"   "+spreadsheet.getPlainTextContent());
            String row = spreadsheet.getPlainTextContent();
            String []split =  row.split(",");
            product[i] = new Product();
            product[i].setName(spreadsheet.getTitle().getPlainText().toString());
            product[i].setVersion(split[0].replace("version:",""));
            product[i].setLoccoveragemaximum(split[1].replace("loccoveragemaximum:",""));
            product[i].setLoccoveragecurrentmonth(split[2].replace("loccoveragecurrentmonth:",""));
            product[i].setClasscoveragemaximum(split[3].replace("classcoveragemaximum:",""));
            product[i].setClasscoveragecurrentmonth(split[4].replace("classcoveragecurrentmonth:",""));
            i++;
        }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return product;

    }



    private int productCount(){
        int proCount = 0;
        try {
            URL SPREADSHEET_FEED_URL = new URL(properties.getProperty("URL"));

            // Make a request to the API and get all spreadsheets.

            ListFeed feed = service.getFeed(SPREADSHEET_FEED_URL,  ListFeed.class);


            List<ListEntry> spreadsheets = feed.getEntries();

            // Iterate through all of the spreadsheets returned
            for ( ListEntry spreadsheet : spreadsheets) {
                // Print the title of this spreadsheet to the screen
                proCount ++;
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return proCount;
    }

    private void authenticate()  {
        try {

            //get properties from constans.properties file
        String USERNAME = properties.getProperty("USERNAME")     ;
        String PASSWORD = properties.getProperty("PASSWORD")     ;

        service = new SpreadsheetService("MySpreadsheetIntegration-v1");

        service.setUserCredentials(USERNAME, PASSWORD);

        } catch (AuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}