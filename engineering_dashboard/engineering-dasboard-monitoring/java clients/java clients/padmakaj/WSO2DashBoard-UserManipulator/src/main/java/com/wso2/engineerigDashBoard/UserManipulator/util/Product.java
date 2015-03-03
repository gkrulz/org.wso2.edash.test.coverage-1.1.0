package com.wso2.engineerigDashBoard.UserManipulator.util;

/**
 * Created by yashira on 5/8/14.
 */
public class Product {
    private String productName;
    private Member[] memberArr;
    private String productLead;

    public void setProductName(String productName){
        this.productName = productName;
    }

    public String getProductName(){
        return productName;
    }

    public Member[] getMemberArr() {
        return memberArr;
    }

    public void setMemberArr(Member[] memberArr) {
        this.memberArr = memberArr;
    }

    public String getProductLead() {
        return productLead;
    }

    public void setProductLead(String productLead) {
        this.productLead = productLead;
    }
}
