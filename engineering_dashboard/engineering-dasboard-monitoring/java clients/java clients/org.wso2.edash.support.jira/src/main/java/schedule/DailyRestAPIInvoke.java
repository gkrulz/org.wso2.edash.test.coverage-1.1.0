package schedule;

public class DailyRestAPIInvoke {

	public static void main(String args[]) {
		SupportJiraRestAPICall restcall = new SupportJiraRestAPICall();
		restcall.getIssues();
	}

}
