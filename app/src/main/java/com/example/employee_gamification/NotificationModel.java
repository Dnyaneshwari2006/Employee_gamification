package com.example.employee_gamification;

public class NotificationModel {
    private String id;  // Add this field
    private String message;
    private String deadline;
    private String createdAt;
    private boolean seen;
    private String taskId;
    private String projectId;
    private String userid;

    // Default constructor (required for Firebase)
    public NotificationModel() {}

    // Constructor with all fields
    public NotificationModel(String id, String message, String deadline, String createdAt, boolean seen , String taskId, String projectId, String userid) {
        this.id = id;
        this.message = message;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.seen = seen;
        this.taskId = taskId;
        this.projectId= projectId;
        this.userid = userid;
    }
    public void settaskId(String taskId) {
        this.taskId = taskId;
    }
    public String gettaskId() {
        return taskId;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }
    public String getuserid() {
        return userid;
    }


    public void setprojectId(String projectId) {
        this.projectId = projectId;
    }
    public String getprojectId() {
        return projectId;
    }



    // Getter and Setter for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for deadline
    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    // Getter and Setter for createdAt
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for seen
    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}