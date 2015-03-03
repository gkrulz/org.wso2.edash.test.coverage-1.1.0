package com.wso2.engdash.UnresolveCounter;

import com.wso2.engdash.UnresolveCounter.Util.DataBaseConnection;
import com.wso2.engdash.UnresolveCounter.Util.Product;
import com.wso2.engdash.UnresolveCounter.Util.TG;
import com.wso2.engdash.UnresolveCounter.manager.ManageUnresolve;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yashira on 8/7/14.
 */
public class Main {
    public static void main(String []args){

        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        ManageUnresolve manageUnresolve = new ManageUnresolve(dataBaseConnection);
        List<TG> tgList = manageUnresolve.getProductList();

        if (tgList == null){
            throw new NullPointerException("TG list is empty.");
        }
        else {
            manageUnresolve.getDetails(tgList);
            System.out.println("*******************\tSystem Updated\t**********************");
        }


    }
}
