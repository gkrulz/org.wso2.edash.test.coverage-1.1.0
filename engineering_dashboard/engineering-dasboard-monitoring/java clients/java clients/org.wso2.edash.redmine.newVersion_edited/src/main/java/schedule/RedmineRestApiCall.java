package schedule;

import invoke.APIInvoke;

import java.io.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.Database;

public class RedmineRestApiCall {

    private final String REDMINE_VERSIONS = "REDMINE_VERSIONS";
    private final String REDMINE_PROJECTS = "REDMINE_PROJECTS";
    private final String REDMINE_ISSUE_LIST = "REDMINE_ISSUE_LIST";
    private final String REDMINE_ISSUE = "REDMINE_ISSUE";
    private final String REDMINE_USERS = "REDMINE_USERS";
    private final String CONSTANTS_PROPERTIES = "";

    Database database;
    private Properties prop = new Properties();
    private String total_count;
    private String assignee;
    private String milestone;
    private String release_start_date;
    private APIInvoke restService;
    static final Logger LOGGER = LogManager.getLogger(RedmineRestApiCall.class.getName());

    public RedmineRestApiCall() {
        database = new Database();
        database.cpTable(); // get a copy of the table
        database.deleteOrginalTables();  // delete Original Table
        loadProperties(); // load properties
        restService = new APIInvoke();
    }

    public void loadProperties() {
        // pr method
        LOGGER.debug("Loading Properties");
        try {

            prop.load(RedmineRestApiCall.class.getClassLoader()
                    .getResourceAsStream("redmine-constants-url.properties"));
            LOGGER.debug("Properties loaded");
        } catch (IOException e) {
            // Error while loading properties
            LOGGER.error("ERROR WHILE LOADING PROPERTIES");
            e.printStackTrace();
        }
    }

    public void getProjectDetails() {
        LOGGER.debug("Getting Projects details...");
        ArrayList<Product> products = new ArrayList<Product>();

        // Set URL
        String projectsUrl = prop.getProperty(REDMINE_PROJECTS);

        APIInvoke restAPI = new APIInvoke();
        // send projects URL to restAPI and receive Json String
        //LOGGER.debug("Requesting Projects JSON");
        String projectJson = restService.invoke(projectsUrl);

        //LOGGER.debug("Projects JSON received\n");

        //-------------------------------------------------------------------------------------------

        JSONObject jsonObject;

        try {
            // convert String to Json object
            jsonObject = new JSONObject(projectJson);

            // get all projects to JsonArray
            JSONArray jsonArray = jsonObject.getJSONArray("projects");

            // for every
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = (JSONObject) jsonArray.get(i);

                String engProjects = temp.get("name").toString();

                try {
                    String parentName = temp.getJSONObject("parent")
                            .get("name").toString();

                    if (parentName.contains("A-WSO2 Platform")
                            && engProjects.contains("WSO2")) {

                        // Skip if PROJECT NAME is equal to "A-WSO2 Platform"
                        if (!engProjects.equals("A-WSO2 Platform")) {
                            Product product = new Product();

                            product.setName(engProjects); // set name of the product
                            product.setIdentifier(temp.get("identifier")
                                    .toString()); // set product identifier

                            product.setId(temp.get("id").toString());   //set product ID

                            // Get Redmine Versions
                            product = getRedmineVersions(product);

                            products.add(product);

                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("JSON value => {}\n{}", engProjects, ex.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //===========================  Old code to Add to DB ========================


        // Add values to Arraylist
        for (int i = 0; i < products.size(); i++) {

            LOGGER.debug("Product added\n{}\n", products.get(i).toString());

            //System.out.print(products.get(i).getSubject() + "\n");

            for (int j = 0; j < products.get(i).release.size(); j++) {

                products.get(i).release.get(j).setOld_done_ratio(
                        database.doneRatio(products.get(i).getName(),
                                products.get(i).release.get(j).getName()));

                database.insertDB(products.get(i).getName(),
                        products.get(i).release.get(j));

                /*System.out.print("       "
                        + products.get(i).release.get(j).getSubject() + "    "
                        + products.get(i).release.get(j).getEndDate() + "   "
                        + products.get(i).release.get(j).getEssimated() + "   "
                        + products.get(i).release.get(j).getPercentage()
                        + "   "
                        + products.get(i).release.get(j).getActual_open()
                        + "   "
                        + products.get(i).release.get(j).getActual_closed());*/
            }

            //System.out.print("\n");
        }

        database.deleteDb();


        // ===================================== new code of add to DB ====================================//

        /*
        for(Product product: products){

            for (int j = 0; j < product.release.size(); j++) {

                product.release.get(j).setOld_done_ratio(
                        database.doneRatio(product.getSubject(),
                                product.release.get(j).getSubject()));

                database.insertDB(product.getSubject(),
                        product.release.get(j));
            }

        }

        database.deleteDb();
        */

        // -----------------------------------------------------------------------------------------------//


    }

    public Product getRedmineVersions(Product product) {

        String projectsVersionsURL = prop.getProperty(REDMINE_VERSIONS);

        //LOGGER.debug("Requesting Redmine versions of Product => {} , Identifier {}", product.getSubject(), product.getIdentifier());
        // Format PROJECT VERSION URL with PRODUCT IDENTIFIER
        String formattedProjectsVersionsURL = MessageFormat.format(projectsVersionsURL, product.getIdentifier());
        //LOGGER.debug("Formatted URL {}", formattedProjectsVersionsURL);
        APIInvoke restAPI = new APIInvoke();
        String projectVersionsJSON = restService.invoke(formattedProjectsVersionsURL);

        //LOGGER.debug("JSON versions received\n");

        /*
        LOGGER.info("Received versions of Product => {} , Identifier {} \nResult :\n\n{} \n\n----------  end of versions JSON  ----------\n",
                product.getSubject(), product.getIdentifier(), projectVersionsJSON);
        */

        JSONObject jsonObject;

        try {

            jsonObject = new JSONObject(projectVersionsJSON);
            JSONArray jsonArray = jsonObject.getJSONArray("versions");

            for (int j = 0; j < jsonArray.length(); j++) {

                JSONObject temp = (JSONObject) jsonArray.get(j);

                if (temp.get("status").equals("open")) {

                    Release release = new Release();

                    String releaseName = temp.get("name").toString();
                    release_start_date = "";
                    try {
                        release.setEndDate(temp.get("due_date").toString());

                        JSONArray array = temp.getJSONArray("custom_fields");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jobject = array.getJSONObject(i);
                            if (jobject.get("name").equals("Start Date")) {

                                release.setStartdate(jobject.get("value")
                                        .toString());
                                release_start_date = release.getStartdate();
                            }
                            if (jobject.get("name").equals("Carbon Version")) {

                                release.setCarbonVersion(jobject.get("value")
                                        .toString());
                            }
                        }
                    } catch (JSONException e) {
                        // e.printStackTrace();
                    }

                    String releaseId = temp.get("id").toString();

                    if (releaseName.contains("WSO2")) {
                        releaseName = releaseName.replaceAll("WSO2", "");
                    }
                    // releaseName = releaseName.replaceAll("[^\\d.]", "");
                    release.setName(releaseName);

                    String arguments[] = new String[2];
                    arguments[0] = product.getId();
                    arguments[1] = releaseId;

                    //==================================
                    //========= GET ISSUE LIST =========
                    //==================================
                    release = getIssueList(arguments, release, product.getName());

                    System.out.print("\n ======= Product Name: " + product.getName() + " ======= \n");

                    product.release.add(release);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return product;
        }

    }

    public Release getIssueList(String[] arguments, Release release, String productName) {

        String issuesListURL = prop.getProperty(REDMINE_ISSUE_LIST);
        String formattedIssuesListURL = MessageFormat.format(issuesListURL, arguments);
        //LOGGER.debug("Formatted Issues List URL :\n{}", formattedIssuesListURL);
        APIInvoke restAPI = new APIInvoke(); // Remake this
        String JsonResult = restService.invoke(formattedIssuesListURL);
        //LOGGER.debug("Received Issues LIST\n");
        /*
        LOGGER.debug("Received Issues List\n\n{}\n\n-------------- end of issues list JSON ---------------\n" ,
                JsonResult);
        */

        JSONObject jsonObject;

        try {

            jsonObject = new JSONObject(JsonResult);

            total_count = jsonObject.get("total_count").toString();

            JSONArray jsonArray = jsonObject.getJSONArray("issues");
            //System.out.print(jsonArray + "\n"); // Change to LOGGER


            int done_ratio = 0;
            double estimated = 0;
            int actual_hours_open = 0;
            int actual_hours_closed = 0;
            int countNew = 0;
            int countClosed = 0;
            int countIMS = 0;

            //System.out.print(release.getSubject() + "\n"); // Change to LOGGER


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject temp = (JSONObject) jsonArray.get(i);

                // System.out.print(jsonArray.length()
                // + "&&&&&&&&&&&&&@@@@@@@@@@@" + temp);

                String issueId = temp.get("id").toString();

                Issue issue = getIssue(issueId);

                // =================== Insert Issues ==============================

                database.insertIssueDetails(productName, release.getName(), issue);
                // =====================================================================


                try {
                    done_ratio += Integer.parseInt(issue.getDone_ratio());

                } catch (Exception e) {
                    // e.printStackTrace();
                }

                try {
                    estimated += issue.getEstmatedHours();

                } catch (Exception e) {
                    // e.printStackTrace();
                }
                try {

                    actual_hours_open += issue.getActual_open();

                } catch (Exception e) {
                    // e.printStackTrace();
                }
                try {

                    actual_hours_closed += issue.getActual_closed();

                } catch (Exception e) {
                    // e.printStackTrace();
                }
                System.out.print(issue.getStatus());
                if (issue.getStatus().equals("New")) {

                    countNew += 1;
                }

                if (!issue.getStatus().equals("New")
                        && !issue.getStatus().equals("Rejected")
                        && !issue.getStatus().equals("Deferred")
                        && !issue.getStatus().equals("Closed")) {
                    countIMS += 1;
                }

                if (issue.getStatus().equals("Closed")) {
                    countClosed += 1;
                }

                release.issue.add(issue);

//                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + issue.toString());

            }
            int issuesFull = (countNew + countClosed + countIMS);
            int dencity = 0;
            if (issuesFull != 0) {
                dencity = (actual_hours_closed + actual_hours_open)
                        / issuesFull;
            }
            release.setDencity(dencity);
            release.setNewIssuesCount(countNew);
            release.setClosedIssuesCount(countClosed);
            release.setIsIssuesCount(countIMS);
            release.setActual_open(Integer.toString(actual_hours_open));
            release.setActual_closed(Integer.toString(actual_hours_closed));
            release.setEssimated(Double.toString(estimated / 24));

            release.setPercentage(Integer.toString(done_ratio
                    / (countClosed + countIMS + countNew)));

            System.out.println("Name " + release.getName() + " New "
                    + release.getNewIssuesCount() + " Closed "
                    + release.getClosedIssuesCount()
                    + " Implimentation Started " + release.getIsIssuesCount()
                    + " dencity  " + release.getDencity());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return release;
        }

    }

    public Issue getIssue(String id) {

        Issue issue = new Issue();

        String projectsV = prop.getProperty(REDMINE_ISSUE);

        String issues = MessageFormat.format(projectsV, id);
        //LOGGER.debug("Formatted issue URL :\n{}\n", issues);

        APIInvoke restAPI = new APIInvoke();
        String issuesJson = restService.invoke(issues);
        //LOGGER.debug("Received Issue JSON");

        /*
        LOGGER.debug("Received Issue JSON : \n\n{}\n\n-------------- End of Issues JSON ---------------\n",
                issuesJson);
        */

        JSONObject jsonObject;

        try {

            jsonObject = new JSONObject(issuesJson);

            JSONObject jsonIssueObject = jsonObject.getJSONObject("issue");

            /*
            LOGGER.debug("Received Issue JSON : \n\n{}\n\n-------------- End of Issues JSON ---------------\n",
                    jsonIssueObject);
            */

            /*
            * key @
            * name @
            * status @
            * assignee @
            * milestone
            * estimatedHours
            * done_ratio @
            * actual_open
            * actual_closed
            * description @
            * */

            issue.setKey(jsonIssueObject.get("id").toString());
            issue.setSubject(jsonIssueObject.get("subject").toString());
            issue.setStatus(jsonIssueObject.getJSONObject("status").get("name").toString());
            issue.setDone_ratio(jsonIssueObject.get("done_ratio").toString());
            issue.setDescription(jsonIssueObject.get("description").toString());

            // Set ASSIGNEE to Issue

            try {
                String userId = jsonIssueObject.getJSONObject("assigned_to").get("id").toString();
                assignee = getEmail(userId);
                issue.setAssignee(assignee);
            } catch (Exception ex) {
                //LOGGER.error("No assignee for {}",issue.getKey());
            }

            // =================== SET Delivered in Milestone ========================
            JSONArray custom_fields = jsonIssueObject.getJSONArray("custom_fields");
            for (int i = 0; i < custom_fields.length(); i++) {
                JSONObject deliveredInMilestone = (JSONObject) custom_fields.get(i);
                String tempDeliveredInMilestone = deliveredInMilestone.get("name").toString();
                // If custom field is equal to "Delivered in Milestone" , set it
                if (tempDeliveredInMilestone.equals("Delivered in Milestone")) {
                    issue.setMilestone(deliveredInMilestone.get("value").toString());
                }
            }

            try {

                // estimated_hours += Double.parseDouble(jsonObject2.get(
                // "estimated_hours").toString());
                // issue_edays = Double.toString(estimated_hours);
                issue.setEstmatedHours(Double.parseDouble(jsonIssueObject.get("estimated_hours").toString()));
                LOGGER.debug("Estimated Hours set : {}", issue.getEstmatedHours());
            } catch (Exception e) {

            }

            // if NOT REJECTED or if DEFERRED
            if (!(issue.getStatus().equals("Rejected") || issue.getStatus().equals("Deferred"))) {
                // if NOT NEW
                if (!issue.getStatus().equals("New")) {
                    // get all the journals to an array
                    JSONArray journals = (JSONArray) jsonIssueObject.get("journals");

                    String created_date = "false";
                    String closed_date = "false";

                    for (int i = 0; i < journals.length(); i++) {

                        JSONObject details = (JSONObject) journals.get(i);
                        // get all the details to an array
                        JSONArray detailsArray = details.getJSONArray("details");

                        for (int j = 0; j < detailsArray.length(); j++) {

                            JSONObject detailsFull = detailsArray.getJSONObject(j);

                            if (detailsFull.get("name").equals("done_ratio")) {
                                if (detailsFull.get("new_value").equals("100")) {

                                    closed_date = details.get("created_on").toString();

                                } else if (detailsFull.get("old_value").equals("0")) {
                                    created_date = details.get("created_on").toString();
                                }
                            }
                        }
                    }

                    System.out.print(created_date + "******************%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                    if (closed_date.equals("false") && issue.getStatus() != "New") {
                        created_date = release_start_date;
                        System.out.print("Implementation Started Days\n");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        created_date = created_date.replace("T", " ");
                        created_date = created_date.replace("Z", "");
                        closed_date = closed_date.replace("T", " ");
                        closed_date = closed_date.replace("Z", "");

                        try {

                            Date date1 = sdf.parse(created_date);
                            Date date2 = new Date();
                            int num = 0;
                            // = (double) ((date2.getTime() - date1
                            // .getTime()) / (1000 * 60 * 60 * 24));
                            Calendar start = Calendar.getInstance();

                            start.setTime(date1);
                            Calendar end = Calendar.getInstance();

                            end.setTime(date2);

                            while (!start.after(end)) {

                                String getTime = start.getTime().toString();
                                if (!(getTime.contains("Sat") || getTime
                                        .contains("Sun"))) {
                                    num += 1;
                                }

                                start.add(Calendar.DATE, 1);

                            }
                            System.out.print("Number of days Implementation started " + num + " Days \n");
                            issue.setActual_open(num);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if ((!closed_date.equals("false") && issue.getStatus() != "New")) {

                        created_date = release_start_date;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        created_date = created_date.replace("T", " ");
                        created_date = created_date.replace("Z", "");
                        closed_date = closed_date.replace("T", " ");
                        closed_date = closed_date.replace("Z", "");

                        try {

                            Date date1 = sdf2.parse(created_date);
                            Date date2 = sdf.parse(closed_date);
                            int num = 0;
                            Calendar start = Calendar.getInstance();

                            start.setTime(date1);
                            Calendar end = Calendar.getInstance();

                            end.setTime(date2);

                            while (!start.after(end)) {

                                String getTime = start.getTime().toString();
                                if (!(getTime.contains("Sat") || getTime.contains("Sun"))) {
                                    num += 1;
                                }
                                // System.out.print(getTime + " DATE \n");
                                start.add(Calendar.DATE, 1);

                            }
                            System.out.print("Closed " + num + " Days \n");
                            // num = num + 1;
                            issue.setActual_closed(num);

                            // closed_issues_total_days += num;
                            // issue_adays = Double.toString(num);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

            // updateDbAllIssues(project_name, assignee, release_Name, issue_id,
            // milestone, issue_description, issue_status, issue_edays,
            // issue_adays);
            // System.out.println("\n" + project_name + "       " + release_Name
            // + "         " + assignee + "        " + issue_status
            // + "       " + issue_id + "       " + issue_description
            // + "  " + milestone + "   Adsys = " + issue_adays
            // + "  EDays= " + issue_edays);
        } catch (JSONException e) {
            //LOGGER.error("JSONException : {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            //LOGGER.error("Exception : {}", e.getMessage());
        } finally {
            //LOGGER.debug("New Issue Added:\n{}",issue.toString());
            //  =========== ADD ISSUE TO DATABASE ===================


            return issue;
        }

    }

    public String getEmail(String id) {
        String email = "";
        String projectsV = prop.getProperty(REDMINE_USERS);
        String issues = MessageFormat.format(projectsV, id);
        APIInvoke restAPI = new APIInvoke();
        String UserJson = restService.invoke(issues);
        JSONObject jsonObject;

        try {

            jsonObject = new JSONObject(UserJson);
            email = jsonObject.getJSONObject("user").get("mail").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }
    //
    // public void updateDbAllIssues(String project_name, String assignee,
    // String realese_name, String issue_id, String milestone,
    // String issue_description, String issue_status, String issue_edays,
    // String issue_adays) {
    // String sql =
    // "INSERT INTO REDMINE_ISSUES_DETAILS (project_name,assignee,realese_name,issue_id,milestone,issue_description,issue_status,issue_edays,issue_adays) VALUES (?,?,?,?,?,?,?,?,?);";
    //
    // // publisher.dataPublish(userIssues);
    //
    // try {
    // Class.forName(db_driver).newInstance();
    //
    // Connection con = DriverManager.getConnection(db_url, db_username,
    // db_password);
    //
    // preparedStatement = con.prepareStatement(sql);
    //
    // preparedStatement.setString(1, project_name);
    //
    // preparedStatement.setString(2, assignee);
    // preparedStatement.setString(3, realese_name);
    //
    // preparedStatement.setString(4, issue_id);
    // preparedStatement.setString(5, milestone);
    //
    // preparedStatement.setString(6, issue_description);
    // preparedStatement.setString(7, issue_status);
    //
    // preparedStatement.setString(8, issue_edays);
    // preparedStatement.setString(9, issue_adays);
    //
    // preparedStatement.executeUpdate();
    // System.out
    // .println(".......................DB-UPDATED...............................");
    // if (!con.getAutoCommit()) {
    // con.commit();
    // }
    // con.close();
    //
    // } catch (InstantiationException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ClassNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    // public void deleteTable() {
    // String sql = "DELETE FROM REDMINE_ISSUES_DETAILS;";
    //
    // // publisher.dataPublish(userIssues);
    //
    // try {
    // Class.forName(db_driver).newInstance();
    //
    // Connection con = DriverManager.getConnection(db_url, db_username,
    // db_password);
    //
    // preparedStatement = con.prepareStatement(sql);
    //
    // preparedStatement.execute();
    // if (!con.getAutoCommit()) {
    // con.commit();
    // }
    // con.close();
    //
    // } catch (InstantiationException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ClassNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

}
