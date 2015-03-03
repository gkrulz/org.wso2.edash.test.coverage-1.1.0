package schedule;

import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        long sTime = System.nanoTime();
        System.out.println("====================================\n    Program Start! \n=====================================");

        //RedmineRestApiCall apiCall = new RedmineRestApiCall();
        //apiCall.getProjectDetails();


        RedmineApiCall testAPI = new RedmineApiCall();
        testAPI.getProjectDetails();


        long time = System.nanoTime() - sTime;
        long minutes = TimeUnit.NANOSECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toNanos(minutes);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
        StringBuilder sb = new StringBuilder();
        sb.append(minutes);
        sb.append(" Minutes | ");
        sb.append(seconds);
        sb.append(" Seconds");

        System.out.println("====================================\n    Program End! \n=====================================\n\n");
        System.out.println(sb.toString());


    }

}
