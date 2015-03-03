package com.wso2.edash.patchcollector;

import com.wso2.edash.patchcollector.manager.PatchController;
import com.wso2.edash.patchcollector.util.DataBaseConnector;

/**
 * Created by yashira on 7/1/14.
 */
public class Main {
    public static void main(String args[]){
        DataBaseConnector connector = new DataBaseConnector();
        PatchController controller = new PatchController(connector);
        controller.countPatches();
        connector.closeConnection();
        System.out.println("*********** System updated ****************");
        System.exit(0);
    }
}
