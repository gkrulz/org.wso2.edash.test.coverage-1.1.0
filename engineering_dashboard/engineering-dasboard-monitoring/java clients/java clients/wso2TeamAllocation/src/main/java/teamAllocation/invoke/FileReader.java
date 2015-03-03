package teamAllocation.invoke;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import teamAllocation.util.Components;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;


public class FileReader {


    private Properties properties;
    private SpreadsheetService service ;

    public int getCount() {
        return count;
    }

    public int count;
    public FileReader()  {

    properties = new Properties();
        try {
            properties.load(FileReader.class.getClassLoader()
                        .getResourceAsStream("constants.properties"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    public Components[] productlist(){
        authenticate();
        int proCount = productCount();


        Components product[] = new Components[proCount];

        try {

        URL SPREADSHEET_FEED_URL = new URL( properties.getProperty("URL"));

        // Make a request to the API and get all spreadsheets.

        ListFeed feed = service.getFeed(SPREADSHEET_FEED_URL,  ListFeed.class);


        List<ListEntry> spreadsheets = feed.getEntries();
        int i = 0;
        // Iterate through all of the spreadsheets returned
        for (ListEntry spreadsheet : spreadsheets) {

            // Print the title of this spreadsheet to the screen


            String row = spreadsheet.getPlainTextContent();



            product[i] = new Components();
            if(row.contains(",")){
                System.out.println(spreadsheet.getTitle().getPlainText()+"     "+spreadsheet.getPlainTextContent());
                String []split =  row.split(",");
                                   if(split.length>2){
            product[i].setProductName(spreadsheet.getTitle().getPlainText());

            int teamAllocation =0;
            for(int j=0;j<split.length;j++){
              String weekCount []= split[j].split(":");
              weekCount[1]=weekCount[1].replace(" ","");

                if(j>2){
                    System.out.println(weekCount[1]);
                teamAllocation +=Integer.parseInt(weekCount[1]);
                }
                }
            teamAllocation = teamAllocation/(split.length-2);
            product[i].setTeamAllocation(Integer.toString(teamAllocation));
                                   }else{
                                       product[i].setProductName(spreadsheet.getTitle().getPlainText());
                                       product[i].setTeamAllocation("0");
                                   }

            }else{

                product[i].setProductName(spreadsheet.getTitle().getPlainText());
                product[i].setTeamAllocation("0");
            }
            i++;

        }
            count =i;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }catch(ArrayIndexOutOfBoundsException e){

        }

        catch (ServiceException e) {
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