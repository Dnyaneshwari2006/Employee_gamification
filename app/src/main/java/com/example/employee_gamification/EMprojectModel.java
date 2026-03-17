package com.example.employee_gamification;

public class EMprojectModel {
    private String projectId;
    private String projectName;
    private String currentUserId;

    public EMprojectModel(String projectId, String projectName , String currentUserId) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.currentUserId = currentUserId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getcurrentUserId() {
        return currentUserId;
    }

    public String getProjectName() {
        return projectName;
    }
}
