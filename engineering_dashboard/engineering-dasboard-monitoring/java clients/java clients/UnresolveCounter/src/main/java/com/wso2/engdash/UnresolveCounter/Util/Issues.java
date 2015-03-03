package com.wso2.engdash.UnresolveCounter.Util;

/**
 * Created by yashira on 8/18/14.
 */
public class Issues {
    private String issueKey;
    private long issueOpenDays;
    private String createdDate;

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public long getIssueOpenDays() {
        return issueOpenDays;
    }

    public void setIssueOpenDays(long issueOpenDays) {
        this.issueOpenDays = issueOpenDays;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
