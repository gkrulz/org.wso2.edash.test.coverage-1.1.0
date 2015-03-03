package com.wso2.engineerigDashBoard.UserManipulator.util;

import com.wso2.engineerigDashBoard.UserManipulator.invoke.FileReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Yashira on 5/8/14.
 */
public class UtilLoader {
    private Properties properties = new Properties();
    private ArrayList<TG> tgArrayList;
    private String tgColArray[];
    private ArrayList<Product> productArrayList;
    private int excludeCol = 1;
    private  String userDir = "";

    public UtilLoader(){
        userDir = System.getProperty("user.dir");
        loadProperties();
    }

    public ArrayList<TG> getTGCollection() throws RuntimeException{
        if(this.tgArrayList.size() == 0){
            throw new RuntimeException("TG is not defined");
        }
        else{
            return this.tgArrayList;
        }
    }
    public void loadProperties(){
        try{
            String userDir = System.getProperty("user.dir");
            File file = new File(userDir+"/properties/util.properties");

            if(file.exists()){
                properties.load(new FileInputStream(file));
            }
            else{
                throw new FileNotFoundException("Cannot locate userfile.properties file in given location.");
            }

            tgColArray = properties.getProperty("TG_ARRAY").split(",");
        }
        catch (FileNotFoundException fileNotFound){
            System.out.println(fileNotFound.getMessage());
            System.exit(1);
        }
        catch(IOException ioException){
            System.out.println("userfile cannot locate");
            System.exit(1);
        }
    }

    public void setTg(FileReader readFile){
        tgArrayList = new ArrayList<TG>();
        int tgRow = Integer.parseInt(properties.getProperty("TG_ROW"));
        int minCol = Integer.parseInt(properties.getProperty("TG_MIN_COL"));
        int maxCol;

        //Product Variables
        int productRow = Integer.parseInt(properties.getProperty("PRODUCT_ROW"));


        for(int z = 0; z < tgColArray.length; z++){
           int proDuctCol = minCol;
           maxCol = (Integer.parseInt(tgColArray[z])+minCol) - 1;
           TG tg = new TG();
           String sample = readFile.readSheet(tgRow, tgRow, minCol, maxCol);
           tg.setName(sample);
           tg.setNumOfProducts(Integer.parseInt(tgColArray[z]));
           System.out.println(sample);
           productArrayList = new ArrayList<Product>();
            //Get particular products for a tg
            for(int y=0;y < Integer.parseInt(tgColArray[z]);y++){
                String productName = readFile.readSheet(productRow, productRow, proDuctCol, proDuctCol);
                String product_leader = "";

                Member []memberarr = new Member[Integer.parseInt(properties.getProperty("TOTAL_TEAM_MEMBERS_ROW"))];
                System.out.println("\t"+productName);

                //Getting members per each product
                int rowOfUserStart = Integer.parseInt(properties.getProperty("USER_ROW"));
                int totalUsersRow = Integer.parseInt(properties.getProperty("TOTAL_TEAM_MEMBERS_ROW"));
                int totalUsers = readFile.readCalculatedValue(totalUsersRow, totalUsersRow, proDuctCol, proDuctCol);

                for(int x = 0;x < totalUsers;x++){
                    String user = readFile.readSheet((x+rowOfUserStart), (x+rowOfUserStart), proDuctCol, proDuctCol);
                    if(!user.equals("")){
                        Member member = new Member();
                        member.setName(user);
                        memberarr[x] = member;
                        System.out.println("\t\t"+user);
                        if(x == 0){
                            product_leader = user;
                        }
                    }
                    else{
                        totalUsers+=1;
                    }

                }
                System.out.println();
                //Adding product values
                Product product = new Product();
                product.setProductName(productName);
                product.setMemberArr(memberarr);
                product.setProductLead(product_leader);
                productArrayList.add(product);
                proDuctCol++;
            }

           minCol = maxCol + 1;
           tg.setNumOfProducts(productArrayList.size());
           tg.setProducts(productArrayList);
           tgArrayList.add(tg);
       }
    }

}
