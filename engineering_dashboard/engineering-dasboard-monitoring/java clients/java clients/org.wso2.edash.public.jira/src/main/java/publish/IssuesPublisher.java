package publish;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.NoStreamDefinitionExistException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import javax.security.sasl.AuthenticationException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
import java.lang.String;

public class IssuesPublisher {
	private Logger logger = Logger.getLogger(IssuesPublisher.class);
	public final String JIRA_ISSUES_STREAM = "org.wso2.bam.jira.issues";
	public final String VERSION = "1.0.0";

	public DataPublisher dataPublisher = null;
	String streamId = null;
    public PrintLog log = new PrintLog();
	public void dataPublish(String[] dataArray)

			throws AgentException,
			MalformedStreamDefinitionException,
			StreamDefinitionException,
			DifferentStreamDefinitionAlreadyDefinedException,
			MalformedURLException,
			AuthenticationException,
			NoStreamDefinitionExistException,
			TransportException,
			SocketException,
			org.wso2.carbon.databridge.commons.exception.AuthenticationException,
			UnsupportedEncodingException {
		System.out.println("Starting BAM JIRA Issues client");
        log.write("Starting BAM JIRA Issues client\n");
		AgentConfiguration agentConfiguration = new AgentConfiguration();
		String currentDir = System.getProperty("user.dir");
		System.out.println("current:" + currentDir);
        log.write("current:" + currentDir+"\n");
		// System.setProperty("javax.net.ssl.trustStore", currentDir +
		// "/repository/resources/security/client-truststore.jks");
		System.setProperty("javax.net.ssl.trustStore", currentDir
                + "/resources/client-truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
		Agent agent = new Agent(agentConfiguration);
		String host;
		boolean publishing = false;

		if (getLocalAddress() != null) {
			host = getLocalAddress().getHostAddress();
		} else {
			host = "localhost"; // Defaults to localhost
		}

		String port = "7612";
		String username = "admin";
		String password = "admin";

		// create data publisher


		if (dataPublisher == null) {
			dataPublisher = new DataPublisher("tcp://" + host + ":" + port,
					username, password, agent);

			try {
				streamId = dataPublisher.findStream(JIRA_ISSUES_STREAM,
						VERSION);
				System.out.println("Stream already defined");
                log.write("Stream already defined\n");

			} catch (NoStreamDefinitionExistException e) {
				streamId = dataPublisher
						.defineStream("{"
								+ "  'name':'"
								+ JIRA_ISSUES_STREAM
								+ "',"
								+ "  'version':'"
								+ VERSION
								+ "',"
								+ "  'nickName': 'Jira_Issues',"
								+ "  'description': 'Jira Issues per user',"
								+

								"  'payloadData':["
								+ "          {'name':'issue_type','type':'STRING'},"
								+ "          {'name':'user_id','type':'STRING'},"
								+ "	         {'name':'user_email','type':'STRING'},"
								+ "          {'name':'time_stamp','type':'STRING'},"
								+ "          {'name':'created_Issues','type':'STRING'},"
								+ "          {'name':'resolved_Issues','type':'STRING'}"
								+ "  ]" + "}");
				// //Define event stream
			}
			catch(Exception ex){
				System.out.println("***Exception****" +ex);
                log.write("***Exception****" +ex);
			}
		}

		// Publish event for a valid stream
		if (!streamId.isEmpty()) {
			System.out.println("Stream ID: " + streamId);
            log.write("Stream ID: " + streamId+"\n");
			publishEvents(dataPublisher, streamId, dataArray);

		}

	}

	private void publishEvents(DataPublisher dataPublisher, String streamId,
			String issuesArray[]) throws AgentException {
		Event eventOne = new Event(streamId, System.currentTimeMillis(), null,
				null, new Object[] { issuesArray[0], issuesArray[1],
						issuesArray[2], issuesArray[3], issuesArray[4],
						issuesArray[5] });

		dataPublisher.publish(eventOne);
		System.out.println("pulishing data:" + issuesArray[0] + issuesArray[1]
				+ issuesArray[2] + issuesArray[3] + issuesArray[4]
				+ issuesArray[5]);

        log.write("pulishing data:" + issuesArray[0] + issuesArray[1]
                + issuesArray[2] + issuesArray[3] + issuesArray[4]
                + issuesArray[5]+"\n");
	}

	public InetAddress getLocalAddress() throws SocketException {
		Enumeration<NetworkInterface> ifaces = NetworkInterface
				.getNetworkInterfaces();
		while (ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while (addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
					return addr;
				}
			}
		}

		return null;
	}

	private String getProperty(String name, String def) {
		String result = System.getProperty(name);
		if (result == null || result.length() == 0 || result == "") {
			result = def;
		}
		return result;
	}
}
