package DailyRestInvoke;

/**
 * Created by Padmaka Wijayagoonawardena on 10/13/14.
 * Email - padmakaj@wso2.com
 */

/**
 * This java client is used to get the public Jira priority details.
 */
public class RestApiInvoke {

    static {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("192.168.66.25")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    /**
     *
     * @param args
     * Main method
     */
    public static void main(String args[]) {
        RestApiCall restcall = new RestApiCall();
        restcall.jiraDetails();
    }
}