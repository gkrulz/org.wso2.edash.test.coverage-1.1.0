package schedule;

/**
 * Created by shenavi on 5/21/14.
 */
public class RestApiInvoke {


    static {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("redmine.wso2.com")) {
                            return true;
                        }
                        return false;
                    }
                });
    }




    public static void main(String args[]){
      RestApiCall restcall= new RestApiCall();
      restcall.getMarketingDetails();
    }




}