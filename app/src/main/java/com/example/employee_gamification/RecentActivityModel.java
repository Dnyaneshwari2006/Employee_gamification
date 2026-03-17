package com.example.employee_gamification;

import com.google.firebase.Timestamp;

public class RecentActivityModel {
    private String type;
    private String description;
    private Timestamp timestamp;
    private String employeeId;
    private String employeeName;

    public RecentActivityModel(String type, String description, Timestamp timestamp, String employeeId, String employeeName) {
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    // Getters
    public String getType() { return type; }
    public String getDescription() { return description; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
}
