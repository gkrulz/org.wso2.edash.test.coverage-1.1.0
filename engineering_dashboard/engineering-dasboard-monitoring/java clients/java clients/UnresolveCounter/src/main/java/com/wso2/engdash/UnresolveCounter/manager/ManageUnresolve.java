package com.wso2.engdash.UnresolveCounter.manager;

import com.wso2.engdash.UnresolveCounter.Util.DataBaseConnection;
import com.wso2.engdash.UnresolveCounter.Util.Issues;
import com.wso2.engdash.UnresolveCounter.Util.Product;
import com.wso2.engdash.UnresolveCounter.Util.TG;
import com.wso2.engdash.UnresolveCounter.invoke.Connector;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by yashira on 8/7/14.
 */
public class ManageUnresolve {

    private Properties sysutil = new Properties();
    private Connector conn = new Connector();
    private String localDir = System.getProperty("user.dir")+"/properties/constants.properties";
    //private String localDir = "/home/yashira/IdeaProjects/UnresolveCounter/src/main/resources/constants.properties";

    private String supportJiraUrl;
    private String supportJiraUserName;
    private  String supportJiraPassword;
    private DataBaseConnection dataBaseConnection;



    public ManageUnresolve(DataBaseConnection dbcon){
        dataBaseConnection = dbcon;
        try {
            File constantFile = new File(localDir);

            if (constantFile.exists()) {
                sysutil.load(new FileInputStream(constantFile));

                //Setting support jira credentials and url
                this.supportJiraUrl = sysutil.getProperty("SUPPORT_JIRA_URL");
                this.supportJiraUserName = sysutil.getProperty("SUPPORT_JIRA_USERNAME");
                this.supportJiraPassword = sysutil.getProperty("SUPPORT_JIRA_PASSWORD");

            } else {
                throw new FileNotFoundException("One of the file is not located in the /properties directory.");
            }
        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void getDetails(List<TG> list){
        int count = 0;
        String tgName;
        String pid;
        String productName;
        ArrayList<Product> productList;

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String date = formatter.format(calendar.getTime());

        while (list.size() > count){
            TG tg = list.get(count++);
            tgName = tg.getTgName();
            productList = tg.getProductList();

            for (Product product : productList) {
                productName = product.getProductName();
                pid = product.getPid();

                Object[] obj = {pid};
                String newUrl = MessageFormat.format(supportJiraUrl, obj);
                String jsonString = conn.getJIRADetails(newUrl, supportJiraUserName, supportJiraPassword);
                JSONObject jsonObject = new JSONObject(jsonString);
                product.setUnresolveCount(Integer.parseInt("" + jsonObject.get("total")));

                if(product.getUnresolveCount() > 0){
                    //getting time
                    JSONArray jsonArray = jsonObject.getJSONArray("issues");
                    Issues issues[] = new Issues[jsonArray.length()];
                    for (int i=0;i<jsonArray.length();i++){
                        issues[i] = new Issues();
                        JSONObject issuesObj = jsonArray.getJSONObject(i);
                        issues[i].setIssueKey((String)issuesObj.get("key"));

                        JSONObject jiraIssue = new JSONObject(conn.getJIRADetails(issuesObj.getString("self"),
                                supportJiraUserName, supportJiraPassword));
                        JSONObject fields = jiraIssue.getJSONObject("fields").getJSONObject("created");
                        String createdDate = fields.getString("value");
                        Calendar creadedDateCal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            creadedDateCal.setTime(sdf.parse(createdDate));// all done

                            issues[i].setIssueOpenDays(daysBetween(creadedDateCal, calendar));
                            issues[i].setCreatedDate(sdf.format(creadedDateCal.getTime()));
                        }catch (Exception ex){

                        }

                    }
                    product.setIssues(issues);
                }

                try {
                    for (Issues issues : product.getIssues()) {
                        System.out.println("TG : " + tgName + " Product : " + productName + " Issue key : "+issues.getCreatedDate()+
                                " Open days : " + issues.getIssueOpenDays());
                        dataBaseConnection.insertQuery(updateTable(date,issues.getCreatedDate(),issues.getIssueOpenDays()
                                ,issues.getIssueKey(),productName,pid,tgName));
                    }
                }catch (NullPointerException ex){
                    continue;
                }
                //System.out.println("TG : " + tgName + " Product : " + productName + " Total : " + Integer.parseInt("" + jsonObject.get("total")));
                /*dataBaseConnection.insertQuery(updateTable(date, product.getUnresolveCount(),
                        productName, pid, tgName));*/
            }
        }

    }

    public String updateTable(String update,String createdDate,long openDays,String key,String productName,String pid,String tgName){
        return "Insert into UnresolveCount_Daily(update_date,createdDate,tg,product,pid,issue_key,open_days)"+
                " values ('"+update+"','"+createdDate+"','"+tgName+"','"+productName+"','"+pid+"','"+key+"','"+openDays+"')";
    }

    public List<TG> getProductList(){
        String url = sysutil.getProperty("PRODUCT_URL");
        String jsonObject = conn.connenctNonSecureLine(url);

        JSONArray jsonArray = new JSONObject(jsonObject).getJSONArray("TG");
        List<TG> tgList = new ArrayList<TG>();
       // System.out.println(jsonArray);

        for(int i = 0;i < jsonArray.length();i++){
            //Json object which contain each tg products and other details
            JSONObject tgJson = (JSONObject)jsonArray.get(i);
            //Creating new tg Object
            TG tg = new TG(tgJson.get("tgName").toString());

            //This array will hold the products which the tg contains
            ArrayList<Product> products = new ArrayList<Product>();
            //JSON array which contains the product which each tg owns
            JSONArray jsonProductArray = tgJson.getJSONArray("tgDetails");
            for(int z = 0;z < jsonProductArray.length();z++){
                JSONObject jsonProjectObject = jsonProductArray.getJSONObject(z);
                Product product = new Product();

                if(!jsonProjectObject.get("shortName").toString().equals("")) {
                    product.setPid(jsonProjectObject.get("shortName").toString());
                    product.setProductName(jsonProjectObject.get("pName").toString());
                    products.add(product);
                }
            }

            //add product list to tg
           tg.setProductList(products);

            tgList.add(tg);
        }

        return tgList;
    }

    public long daysBetween(Calendar startDate, Calendar endDate) {
        long start_Date = startDate.getTimeInMillis();
        long end_Date = endDate.getTimeInMillis();

        long timeDifInMilliSec = end_Date - start_Date;

        long timeDifDays = timeDifInMilliSec / (24 * 60 * 60 * 1000);

        return timeDifDays;
    }
}
