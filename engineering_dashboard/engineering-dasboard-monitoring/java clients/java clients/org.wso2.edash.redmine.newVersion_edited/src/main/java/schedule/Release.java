package schedule;

import java.util.ArrayList;

public class Release {

	private String Name;
	public ArrayList<Issue> issue = new ArrayList<Issue>();
	private String Startdate;
	private String endDate;
	private String percentage;
	private String essimated;
	private String actual_open;
	private String actual_closed;
	private int newIssuesCount;
	private int closedIssuesCount;
	private int dencity;
	private String carbonVersion;
	private String old_done_ratio;

	public String getOld_done_ratio() {
		return old_done_ratio;
	}

	public void setOld_done_ratio(String old_done_ratio) {
		this.old_done_ratio = old_done_ratio;
	}

	public String getCarbonVersion() {
		return carbonVersion;
	}

	public void setCarbonVersion(String carbonVersion) {
		this.carbonVersion = carbonVersion;
	}

	public int getDencity() {return dencity;}

	public void setDencity(int dencity) {
		this.dencity = dencity;
	}

	public int getClosedIssuesCount() {
		return closedIssuesCount;
	}

	public void setClosedIssuesCount(int closedIssuesCount) {
		this.closedIssuesCount = closedIssuesCount;
	}

	public int getNewIssuesCount() {
		return newIssuesCount;
	}

	public void setNewIssuesCount(int newIssuesCount) {
		this.newIssuesCount = newIssuesCount;
	}

	public int getIsIssuesCount() {
		return isIssuesCount;
	}

	public void setIsIssuesCount(int isIssuesCount) {
		this.isIssuesCount = isIssuesCount;
	}

	private int isIssuesCount;

	public String getName() {
		if (Name == null) {
			Name = "";
		}
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getStartdate() {
		if (Startdate == null) {
			Startdate = "";
		}
		return Startdate;
	}

	public void setStartdate(String startdate) {
		Startdate = startdate;
	}

	public String getEndDate() {
		if (endDate == null) {
			endDate = "";
		}
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPercentage() {
		if (percentage == null) {
			percentage = "";
		}

		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getEssimated() {
		if (essimated == null) {
			essimated = "";
		}
		return essimated;
	}

	public void setEssimated(String essimated) {
		this.essimated = essimated;
	}

	public ArrayList<Issue> getIssue() {
		return issue;
	}

	public void setIssue(ArrayList<Issue> issue) {
		this.issue = issue;
	}

	public String getActual_open() {
		if (actual_open == null) {
			actual_open = "";
		}
		return actual_open;
	}

	public void setActual_open(String actual_open) {
		this.actual_open = actual_open;
	}

	public String getActual_closed() {
		if (actual_closed == null) {
			actual_closed = "";
		}
		return actual_closed;
	}

	public void setActual_closed(String actual_closed) {
		this.actual_closed = actual_closed;
	}
}
