package com.wso2.jenkins.monitoring;

import com.wso2.jenkins.monitoring.invokeRest.Invoke;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Padmaka Wijayagoonawardena on 1/20/15.
 * Email - padmakaj@wso2.com
 */
public class Main {
    public static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Invoke invoke = new Invoke();
        invoke.getInfo();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        int minutes = (int) (((double)duration / 1000000000.0)/60);
        int seconds = (int) (((double)duration / 1000000000.0) % 60);
        LOGGER.info("Time Spent - " + minutes + ":" + seconds);
    }
}
