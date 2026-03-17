package com.example.employee_gamification;

import com.google.firebase.Timestamp;

import java.util.List;

public class CEO_Project {
    private String id;
    private String projectname;
    private String createdBy;
    private Timestamp deadline;
    private String status; // NEW field added
    private List<String> assignedemployees;

    // Required empty constructor for Firestore deserialization
    public CEO_Project() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectname() { return projectname; }
    public void setProjectname(String projectname) { this.projectname = projectname; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getAssignedemployees() { return assignedemployees; }
    public void setAssignedemployees(List<String> assignedemployees) {
        this.assignedemployees = assignedemployees;
    }
}
