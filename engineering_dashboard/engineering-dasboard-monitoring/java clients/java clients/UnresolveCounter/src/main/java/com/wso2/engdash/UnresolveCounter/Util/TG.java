package com.wso2.engdash.UnresolveCounter.Util;

import java.util.ArrayList;

/**
 * Created by yashira on 8/8/14.
 */
public class TG {
    private String tgName;
    private ArrayList<Product> productList;

    public TG(String name) {
        setTgName(name);
        this.setProductList(new ArrayList<Product>());
    }

    public String getTgName() {
        return tgName;
    }

    public void setTgName(String tgName) {
        this.tgName = tgName;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

}
