package com.wso2.engdash.UnresolveCounter.Util;

/**
 * Created by yashira on 8/8/14.
 */
public class Product {
    private String productName;
    private String pid;
    private int unresolveCount = 0;
    private Issues[] issues;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }


    public int getUnresolveCount() { return unresolveCount; }

    public void setUnresolveCount(int unresolveCount) { this.unresolveCount = unresolveCount; }

    public Issues[] getIssues() {
        return issues;
    }

    public void setIssues(Issues[] issues) {
        this.issues = issues;
    }
}
