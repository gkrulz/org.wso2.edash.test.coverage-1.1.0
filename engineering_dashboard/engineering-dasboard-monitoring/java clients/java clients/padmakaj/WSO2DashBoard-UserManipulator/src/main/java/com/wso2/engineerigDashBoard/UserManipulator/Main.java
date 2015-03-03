package com.wso2.engineerigDashBoard.UserManipulator;

import com.wso2.engineerigDashBoard.UserManipulator.database.DataBaseConnection;
import com.wso2.engineerigDashBoard.UserManipulator.invoke.FileReader;
import com.wso2.engineerigDashBoard.UserManipulator.util.Member;
import com.wso2.engineerigDashBoard.UserManipulator.util.Product;
import com.wso2.engineerigDashBoard.UserManipulator.util.TG;
import com.wso2.engineerigDashBoard.UserManipulator.util.UtilLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Yashira on 6/13/14.
 */
public class Main {

    public static void main(String []args){
        Main main = new Main();
        DataBaseConnection dataBaseConnection = null;
        try {
            FileReader fileReader = main.getFileReader();
            dataBaseConnection = new DataBaseConnection(main.getProperties());
            List<TG> arr = main.initializeUsers(fileReader,dataBaseConnection);


        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        finally {
            if(dataBaseConnection != null){
                dataBaseConnection.shutDownConnection();
            }
        }
    }

    public Properties getProperties()throws FileNotFoundException,IOException{
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir+"/properties/constants.properties");
        if(file.exists()) {
            Properties constantProperties = new Properties();
            constantProperties.load(new FileInputStream(file));
            return constantProperties;
        }
        else{
            throw new FileNotFoundException("Property file cannot be found.");
        }
    }

    public FileReader getFileReader() throws FileNotFoundException,IOException{

            Properties constantProperties = getProperties();
            String url = constantProperties.getProperty("SPREAD_SHEET_URL");

            FileReader reader = new FileReader(url);
            return reader;

    }

    public List<TG> initializeUsers(FileReader fileReader,DataBaseConnection dbConnection) throws FileNotFoundException,IOException{

        UtilLoader utilLoader = new UtilLoader();
        utilLoader.setTg(fileReader);
        DataBaseConnection connection = new DataBaseConnection(getProperties());

        List<TG> tgList = utilLoader.getTGCollection();

        if(tgList.size() == 0){
            System.exit(1);
        }
        else {

            String insertUserQuery = "INSERT INTO SUPPORT_JIRA_USER_LIST(userEmail,productName,tgName,type)" +
                    "VALUES(?,?,?,?)";

            dbConnection.initStatement(insertUserQuery);
            int count = 0;
            for (int i = 0; i < tgList.size(); i++) {

                TG tg = tgList.get(i);
                String tgName = tg.getName();
                List<Product> productList = tg.getProducts();


                for(int z = 0; z < productList.size(); z++){

                    Product product = productList.get(z);
                    String productName = product.getProductName();
                    String productLead = product.getProductLead();
                    Member []memberArray = product.getMemberArr();

                    for(int y = 0; y < memberArray.length; y++){
                        try{
                            String memberEmail = memberArray[y].getName().replaceAll("\\s","").toLowerCase()+"@wso2.com";
                            String type = "SE";
                            if(memberArray[y].getName().equals(productLead)){
                                type = "Product Lead";
                            }

                            dbConnection.setStatement(memberEmail,productName,tgName,type);
                            count++;
                            if ((count + 1) % 1000 == 0) {
                                dbConnection.executeBatch(); // Execute every 1000 items.
                            }

                        }catch (NullPointerException ex){
                            continue;
                        }
                    }
                }
            }
            //Execute the batch
            dbConnection.executeBatch();
        }
        return tgList;
    }


}
