package schedule;

public class Issue {

    private String key;
    private String subject;
    private String status;
    private String assignee;
    private String milestone;
    private String description;
    private Double estmatedHours;
    private String done_ratio;
    private int actual_open;
    private int actual_closed;


    public Issue(){
        key = "";
        subject = "";
        status = "";
        assignee = "";
        milestone = "";
        description = "";
        estmatedHours = 0.0;
        done_ratio = "";
        actual_open = 0;
        actual_closed = 0;
    }

    public int getActual_open() {
        return actual_open;
    }

    public void setActual_open(int actual_open) {
        this.actual_open = actual_open;
    }

    public int getActual_closed() {
        return actual_closed;
    }

    public void setActual_closed(int actual_closed) {
        this.actual_closed = actual_closed;
    }

    public Double getEstmatedHours() {
        return estmatedHours;
    }

    public void setEstmatedHours(Double estmatedHours) {
        this.estmatedHours = estmatedHours;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDone_ratio() {
        return done_ratio;
    }

    public void setDone_ratio(String done_ratio) {
        this.done_ratio = done_ratio;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        String out;

        out =   " | Key: " + key +
                " | Name: " + subject +
                " | Status: " + status +
                " | Assignee: " + assignee +
                " | Milestone: " + milestone +
                " | Estimated H: " + estmatedHours +
                " | Actual open: " + actual_open +
                " | Actual closed: "+ actual_closed;

        return out;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
