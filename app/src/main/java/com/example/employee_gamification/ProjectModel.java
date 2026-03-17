package com.example.employee_gamification;

public class ProjectModel {
    private String projectId;
    private String projectName;

    public ProjectModel(String projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}
