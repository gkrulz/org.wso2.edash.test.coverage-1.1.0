package com.wso2.supportpreview;

import com.wso2.supportpreview.schedule.JiraApiCall;

/**
 * Created by yashira on 5/5/14.
 */
public class Main {
    public static void main(String[] args) {
        try {
            JiraApiCall apiCall = new JiraApiCall();
            apiCall.getResolved();
            System.out.println("**************\t System is updated \t****************");
            System.exit(0);
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
