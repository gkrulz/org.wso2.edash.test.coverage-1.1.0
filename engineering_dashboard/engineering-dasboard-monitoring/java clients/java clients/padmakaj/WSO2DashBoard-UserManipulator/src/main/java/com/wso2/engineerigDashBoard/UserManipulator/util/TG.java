package com.wso2.engineerigDashBoard.UserManipulator.util;

import java.util.ArrayList;

/**
 * Created by yashira on 5/8/14.
 */
public class TG {
    private String name;
    private int numOfProducts;
    private ArrayList<Product> products;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfProducts() {
        return numOfProducts;
    }

    public void setNumOfProducts(int numOfProducts) {
        this.numOfProducts = numOfProducts;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
