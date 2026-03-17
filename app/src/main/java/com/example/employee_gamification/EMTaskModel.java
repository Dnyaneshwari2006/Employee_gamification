package com.example.employee_gamification;

public class EMTaskModel {
    private String userId;
    private String taskId;
    private String taskName;
    private String deadline;
    private String status;
    private boolean isExpanded;  // New field to track expansion state

    private String projectName;

    private String projectID;

    // Constructor
    public EMTaskModel(String userId , String taskId, String taskName, String deadline, String status , String projectName, String projectId) {
        this.userId = userId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.deadline = deadline;
        this.status = status;
        this.isExpanded = false; // Default value
        this.projectName=projectName;
        this.projectID=projectId;
    }

    // Getters

    public String getUserId() {
        return userId;
    }

    public String getprojectName() {
        return projectName;
    }

    public String getprojectID() {
        return projectID;
    }



    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    // Setters (if needed)
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setExpanded(boolean expanded) { isExpanded = expanded; }

}
