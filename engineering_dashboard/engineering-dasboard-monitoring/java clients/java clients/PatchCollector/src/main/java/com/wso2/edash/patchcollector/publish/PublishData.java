package com.wso2.edash.patchcollector.publish;

import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.*;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by yashira on 7/2/14.
 */
public class PublishData {
    public final String PATCHES_COUNT_STREAM = "org.wso2.bam.patches.count";
    public final String VERSION = "1.0.0";

    public DataPublisher publisher;
    String streamId = null;
    String currentDir;

    public PublishData(){
        currentDir = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.trustStore",
                currentDir+"/properties/client-truststore.jks");
       /* System.setProperty("javax.net.ssl.trustStore", "/home/yashira/IdeaProjects/PatchCollector/" +
                "src/main/resources/client-truststore.jks");*/

        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    }

    public void dataPublish(String[] data)throws  MalformedURLException,
            AgentException,AuthenticationException,TransportException,
            MalformedStreamDefinitionException,StreamDefinitionException
            ,DifferentStreamDefinitionAlreadyDefinedException{
        Agent agent = new Agent();

        String host = null;
        boolean hosting = false;

        if(getLocalAddress() != null){
            host = getLocalAddress().getHostAddress();
        }else{
            host = "localhost";
        }

        String port = "7612";
        String username = "admin";
        String password = "admin";

        // create data publisher
        if(publisher == null){
            publisher = new DataPublisher("tcp://" + host + ":" + port,
                    username, password);
            try{
                streamId = publisher.findStream(PATCHES_COUNT_STREAM,VERSION);
                // System.out.println("Stream already defined");
            }catch (NoStreamDefinitionExistException ex){
                streamId = publisher
                        .defineStream("{"
                                + "  'name':'"
                                + PATCHES_COUNT_STREAM
                                + "',"
                                + "  'version':'"
                                + VERSION
                                + "',"
                                + "  'nickName': 'Patches Count',"
                                + "  'description': 'Patch count per user',"
                                +

                                "  'payloadData':["
                                + "          {'name':'issue_type','type':'STRING'},"
                                + "	         {'name':'user_email','type':'STRING'},"
                                + "          {'name':'time_stamp','type':'STRING'},"
                                + "          {'name':'patches_count','type':'STRING'}"
                                + "  ]" + "}");
                // //Define event stream
            }catch (Exception ex){
                ex.printStackTrace();
            }


        }

        // Publish event for a valid stream
        if (!streamId.isEmpty()) {
            publishEvents(publisher, streamId, data);

        }


    }
    public void publishEvents(DataPublisher publisher,String streamId,String []resovelArray)throws AgentException{
        Event event = new Event(streamId, System.currentTimeMillis(), null,
                null, new Object[] { resovelArray[0], resovelArray[1],
                resovelArray[2],resovelArray[3]});
        publisher.publish(event);
    }

    public InetAddress getLocalAddress(){
        try{
            Enumeration<NetworkInterface> iface = NetworkInterface.getNetworkInterfaces();
            while (iface.hasMoreElements()){
                NetworkInterface networkInterface = iface.nextElement();
                Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();

                while (addressEnumeration.hasMoreElements()) {
                    InetAddress addr = addressEnumeration.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }
        }catch(SocketException socketEx){
            System.out.println("Socket exception thrown in ResolveCounter : "+socketEx.getMessage());
        }
        return null;
    }
}

