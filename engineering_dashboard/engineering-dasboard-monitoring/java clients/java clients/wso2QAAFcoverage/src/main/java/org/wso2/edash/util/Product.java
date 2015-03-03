package org.wso2.edash.util;

/**
 * Created by yashira on 5/8/14.
 */
public class Product {
    public String name;
    public String version;
    public String loccoveragemaximum;
    public String loccoveragecurrentmonth;
    public String classcoveragemaximum;
    public String classcoveragecurrentmonth;
    public String functionalcoverage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        version = version.replaceAll("[^\\d.]", "");
        this.version = version;
    }

    public String getLoccoveragemaximum() {
        return loccoveragemaximum;
    }

    public void setLoccoveragemaximum(String loccoveragemaximum) {
        this.loccoveragemaximum = loccoveragemaximum;
    }

    public String getLoccoveragecurrentmonth() {
        return loccoveragecurrentmonth;
    }

    public void setLoccoveragecurrentmonth(String loccoveragecurrentmonth) {
        this.loccoveragecurrentmonth = loccoveragecurrentmonth;
    }

    public String getClasscoveragemaximum() {
        return classcoveragemaximum;
    }

    public void setClasscoveragemaximum(String classcoveragemaximum) {
        this.classcoveragemaximum = classcoveragemaximum;
    }

    public String getClasscoveragecurrentmonth() {
        return classcoveragecurrentmonth;
    }

    public void setClasscoveragecurrentmonth(String classcoveragecurrentmonth) {
        this.classcoveragecurrentmonth = classcoveragecurrentmonth;
    }

    public String getFunctionalcoverage() {
        return functionalcoverage;
    }

    public void setFunctionalcoverage(String functionalcoverage) {
        this.functionalcoverage = functionalcoverage;
    }




}
