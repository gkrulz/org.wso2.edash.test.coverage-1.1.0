package DailyRestInvoke;


public class DailyInvoke {


    static {
        //for localhost testing only
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
        RestCall restcall= new RestCall();
       // restcall.getCount();
       // restcall.getTrainingCount();
        restcall.getTrainings();

    }



}
