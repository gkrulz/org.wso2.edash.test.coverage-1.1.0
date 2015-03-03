package com.wso2.engineerigDashBoard.UserManipulator.invoke;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created by Yashira on 6/13/14.
 */
public class FileReader {
    Properties googleProperties = new Properties();
    private URL spreadsheetUrl;
    private String username = "";
    private String password = "";
    private SpreadsheetService spreadsheetService;
    private String tgarr[];
    private String userDir;

    public FileReader(String url) throws MalformedURLException {
        try {
            userDir = System.getProperty("user.dir");
            File file = new File(userDir+"/properties/constants.properties");
            if(file.exists()){
               googleProperties.load(new FileInputStream(file));
            }
            else {
                throw new FileNotFoundException("constants.properties file does not exist in the given path.");
            }


            this.username = googleProperties.getProperty("WSO2_EMAIL_USERNAME");
            this.password = googleProperties.getProperty("WSO2_EMAIL_PASSWORD");
            this.spreadsheetUrl = new URL(url);
            spreadsheetService = new SpreadsheetService("com.wso2.supportpreview.reader");
            spreadsheetService.setProtocolVersion(SpreadsheetService.Versions.V3);
            authenticate();
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch(IOException ex){
            System.out.println("Exception thrown in read file constructor. "+ex.getMessage());
            System.exit(1);
        }
    }
    public void authenticate(){
        try{
            spreadsheetService.setUserCredentials(username,password);
        }
        catch (AuthenticationException auth){
            System.out.println("Cannot authenticate");
            System.exit(1);
        }
    }
    public String readSheet(int min_row,int max_row,int min_col,int max_col){
        String output = "";
        try{

            SpreadsheetFeed feed = spreadsheetService.getFeed(spreadsheetUrl,SpreadsheetFeed.class);
            List<SpreadsheetEntry> entryList = feed.getEntries();

            //Getting worksheet
            if(entryList.size() == 0){
                //terminate reading by raising an error flag
                System.out.println("Given spreadsheet cannot view.");
                System.exit(1);
            }

            SpreadsheetEntry entry = entryList.get(0);


            WorksheetFeed worksheetFeed = spreadsheetService.getFeed(
                    new URL("https://spreadsheets.google.com/feeds/worksheets/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/private/basic"),WorksheetFeed.class);
            List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
            WorksheetEntry worksheet = worksheets.get(0);



            // Fetch column 4, and every row after row 1.
            URL cellFeedUrl = new URI(new URL("https://spreadsheets.google.com/feeds/cells/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/ocq/private/full").toString()
                    + "?min-row="+min_row+"&max-row="+max_row+"&min-col="+min_col+"&max-col="+max_col+"").toURL();
            CellFeed cellFeed = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
            /*URL cellFeedUrl = new URL("https://spreadsheets.google.com/feeds/cells/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/ocq/private/full");
            CellFeed cellFeed = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);*/

            for (CellEntry cell : cellFeed.getEntries()) {
                output = cell.getCell().getInputValue();
            }


        }
        catch(IOException ioex){
            System.out.println("IOException occurred in ReadFile.readSheet : "+ioex.getMessage());
        }
        catch(ServiceException serviceException){
            System.out.println("Service Exception occurred in ReadFile.readSheet : "+serviceException.getMessage());
            output="end";
        }

        catch (NullPointerException nn){
            nn.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return output;
    }
    public int readCalculatedValue(int min_row,int max_row,int min_col,int max_col){
        int output = 0;
        try{

            SpreadsheetFeed feed = spreadsheetService.getFeed(spreadsheetUrl,SpreadsheetFeed.class);
            List<SpreadsheetEntry> entryList = feed.getEntries();

            //Getting worksheet
            if(entryList.size() == 0){
                //terminate reading by raising an error flag
                System.out.println("Oh darn");
            }

            SpreadsheetEntry entry = entryList.get(0);


            WorksheetFeed worksheetFeed = spreadsheetService.getFeed(
                    new URL("https://spreadsheets.google.com/feeds/worksheets/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/private/basic"),WorksheetFeed.class);
            List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
            WorksheetEntry worksheet = worksheets.get(0);



            // Fetch column 4, and every row after row 1.
            URL cellFeedUrl = new URI(new URL("https://spreadsheets.google.com/feeds/cells/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/ocq/private/full").toString()
                    + "?min-row="+min_row+"&max-row="+max_row+"&min-col="+min_col+"&max-col="+max_col+"").toURL();
            CellFeed cellFeed = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);
            /*URL cellFeedUrl = new URL("https://spreadsheets.google.com/feeds/cells/0AlyX7QJ8Jm8ZdERKbDZISEFlZ0xNTWxubjdkcDMzWlE/ocq/private/full");
            CellFeed cellFeed = spreadsheetService.getFeed(cellFeedUrl, CellFeed.class);*/

            for (CellEntry cell : cellFeed.getEntries()) {
                output = cell.getCell().getNumericValue().intValue();

            }


        }
        catch(IOException ioex){
            System.out.println("IOException occurred in ReadFile.readSheet : "+ioex.getMessage());
        }
        catch(ServiceException serviceException){
            System.out.println("Service Exception occurred in ReadFile.readSheet : "+serviceException.getMessage());
        }

        catch (NullPointerException nn){
            nn.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return output;
    }
}
