package com.example.employee_gamification;


public class HRTaskModel {
    private String taskId;
    private String taskName;
    private String deadline;
    private String status;
    private boolean isExpanded;  // New field to track expansion state
    private String assignedTo;
    private String name;

    // Constructor
    public HRTaskModel(String assignedTo , String taskId, String taskName, String deadline, String status, String name) {
        this.assignedTo = assignedTo;
        this.taskId = taskId;
        this.taskName = taskName;
        this.deadline = deadline;
        this.status = status;
        this.isExpanded = false; // Default value
    }

    // Getters

    public String getassignedTo() {
        return assignedTo;
    }

    public String getname() {
        return name;
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
