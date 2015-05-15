package org.wso2.edash.test.coverage.model;

/**
 * Model class for a Product
 */
public class Product {
	private String name;
	private String version;
	private String loccoveragemaximum;
	private String loccoveragecurrentmonth;
	private String classcoveragemaximum;
	private String classcoveragecurrentmonth;
	private String functionalcoverage;

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
		String ver = version.replaceAll("[^\\d.]", "");
		this.version = ver;
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
