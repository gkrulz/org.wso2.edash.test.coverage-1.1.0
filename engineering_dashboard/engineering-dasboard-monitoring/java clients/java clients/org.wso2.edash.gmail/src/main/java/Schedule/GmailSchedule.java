package Schedule;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import publish.PrintLog;
import DatabaseHanddler.DatabaseConnecter;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

public class GmailSchedule {

	private String protocal;
	private String protocalName;
	private String host;
	private String userName;
	private String password;
	DatabaseConnecter dbobject;
	private PreparedStatement preparedStatement = null;
	PrintLog log;

	public GmailSchedule() {
		dbobject = new DatabaseConnecter();
		createTable();
		Loadproperties();
		log = new PrintLog();
	}

	public void readGemail() {

		getMails("Architecture");
		getMails("Dev");
		getMails("Engineering");
		getMails("Marketing");
		getMails("Strategy");
		getMails("Support");

	}

	public void getMails(String folderName) {
		try {
			Properties props = new Properties();
			props.put(protocal, protocalName);

			Session session;

			session = Session.getDefaultInstance(props, null);

			Store store = session.getStore(protocalName);
			store.connect(host, userName, password);

			IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);

			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

			IMAPMessage[] message = (IMAPMessage[]) folder
					.search(unseenFlagTerm);
			int j = message.length - 1;
			for (int i = j; i >= 0; i--) {
				System.out.println("*************" + message[i]);

				String userName[] = new String[2];

				if (message[i].getFrom()[0].toString().contains("<")) {
					userName = message[i].getFrom()[0].toString().split("<");
					userName[1] = userName[1].replace(">", "");
				} else {
					userName[1] = message[i].getFrom()[0].toString();
				}

				String filter[] = new String[3];
				System.out.println(message[i].getReceivedDate());
				if (message[i].getReceivedDate().toString().contains("PDT")) {
					filter = message[i].getReceivedDate().toString()
							.split("PDT");

					filter[1] = filter[1].replace(" ", "");

					int month = message[i].getReceivedDate().getMonth() + 1;
					int day = message[i].getReceivedDate().getDate();
					String date = filter[1] + "-" + month + "-" + day;
					String issueFormat = "yyyy-MM-dd";

					SimpleDateFormat issueDateFormat = new SimpleDateFormat(
							issueFormat);
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date currdate = new Date();
					String[] currentDate = dateFormat.format(currdate)
							.toString().split("-");

					System.out.println("*****************************Gmail --"
							+ folderName
							+ "--- **************************************");
					log.write("*****************************Gmail --"
							+ folderName
							+ "--- **************************************\n");
					Date getIssueDate;
					int week = 0;
					try {
						getIssueDate = issueDateFormat.parse(date);
						Calendar cal = Calendar.getInstance();
						cal.setTime(getIssueDate);
						week = cal.get(4);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					InsertData(userName[1], folderName,
							Integer.parseInt(filter[1]), month, week, day,
							message[i].getReceivedDate().toString());

					// outerloop:
					//
					// for (int q = 0; q < message[i].getAllRecipients().length;
					// q++) {
					// System.out.println(message[i].getAllRecipients()[q]);
					// String lables[] = { "architecture", "dev",
					// "engineering", "marketing", "strategy",
					// "support" };
					// System.out.println(folderName.toLowerCase());
					// for (int c = 0; c < lables.length; c++) {
					//
					// if (!folderName.toLowerCase().contains(lables[c])) {
					//
					// if (message[i].getAllRecipients()[q].toString()
					// .contains(lables[c])) {
					//
					// System.out
					// .println("Another lable inside email");
					//
					// message[i].setFlag(Flags.Flag.SEEN, false);
					// break outerloop;
					//
					// } else {
					//
					message[i].setFlag(Flags.Flag.SEEN, true);
					// }
					// }
					// }
					//
					// }
					//
				} else {
					message[i].setFlag(Flags.Flag.SEEN, false);
				}

			}

			folder.close(false);
			store.close();
		} catch (MessagingException e) {
			System.out.println("Error: " + e);
		}
	}

	public void Loadproperties() {

		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(GmailSchedule.class.getClassLoader().getResourceAsStream(
					"config.properties"));

			protocal = prop.getProperty("protocal");
			protocalName = prop.getProperty("protocalName");
			host = prop.getProperty("host");
			userName = prop.getProperty("userName");
			password = prop.getProperty("password");

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void InsertData(String user_email, String group, int iyear,
			int imonth, int iweek, int day, String recivedDate) {
		String sql;// =
					// "INSERT INTO GMAIL_DETAILS (user_email,folder_name,iyear,imonth,iweek,iday,recivedDate,attendent) VALUES (?,?,?,?,?,?,?);";
		sql = " INSERT INTO GMAIL_DETAILS (user_email,folder_name,iyear,imonth,iweek,iday,recivedDate,attendent) SELECT ?,?,?,?,?,?,?,? FROM DUAL WHERE NOT EXISTS  (SELECT user_email,folder_name,recivedDate FROM GMAIL_DETAILS WHERE user_email=? AND folder_name= ? AND recivedDate =?)LIMIT 1;";
		// sql =
		// " INSERT INTO SUPPORT_DAILY_DETAILS (user_email,issue_key,projectType,date,iyear,imonth,iweek,iday,timestamp,time_spent,attendent) SELECT * FROM (SELECT ?,?,?,?,?,?,?,?,?,?,?) AS tmp WHERE NOT EXISTS (SELECT user_email,issue_key,date,iyear,imonth,iweek,iday,timestamp FROM SUPPORT_DAILY_DETAILS WHERE user_email = ? AND issue_key = ? AND date = ? AND iyear = ? AND imonth = ? AND iweek = ? AND iday = ? AND timestamp = ?) LIMIT 1;";

		try {
			Class.forName(dbobject.getDatabaseDriver()).newInstance();

			Connection con;

			con = DriverManager.getConnection(dbobject.getDbUrl(),
					dbobject.getDbUserName(), dbobject.getDbPwd());
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, user_email);
			preparedStatement.setString(2, group);
			preparedStatement.setInt(3, iyear);
			preparedStatement.setInt(4, imonth);
			preparedStatement.setInt(5, iweek);
			preparedStatement.setInt(6, day);
			preparedStatement.setString(7, recivedDate);
			preparedStatement.setInt(8, 1);
			preparedStatement.setString(9, user_email);
			preparedStatement.setString(10, group);
			preparedStatement.setString(11, recivedDate);

			preparedStatement.executeUpdate();

			System.out.println("From : " + user_email + " FolderName: " + group
					+ " date:" + recivedDate);
			log.write("From : " + user_email + " FolderName: " + group
					+ " date:" + recivedDate + "\n");
			System.out.println("Year : " + iyear + " Month : " + imonth
					+ " WEEK : " + iweek + " Day : " + day);
			log.write("Year : " + iyear + " Month : " + imonth + " WEEK : "
					+ iweek + " Day : " + day + "\n");
			System.out
					.println("*****************************Gmail --updated--**************************************");
			log.write("*****************************Gmail --updated--**************************************\n");
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTable() {

		String sql = "CREATE TABLE IF NOT EXISTS GMAIL_DETAILS (user_email varchar(255),folder_name varchar(255),iyear int(11),imonth int(11),iweek int(11),iday int(11),recivedDate varchar(255),attendent int(11));";
		try {
			Class.forName(dbobject.getDatabaseDriver()).newInstance();

			Connection con;

			con = DriverManager.getConnection(dbobject.getDbUrl(),
					dbobject.getDbUserName(), dbobject.getDbPwd());

			preparedStatement = con.prepareStatement(sql);
			preparedStatement.executeUpdate();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
