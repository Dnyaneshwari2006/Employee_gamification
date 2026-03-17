package com.example.employee_gamification;

import java.util.Date;

public class TaskModel {
    private String taskTitle;
    private String deadline;
    private String projectName;
    private String formattedDeadline;
    private Date rawDeadline;


    public TaskModel() {} // required

    public TaskModel(String taskTitle, String formattedDeadline, String projectName, Date rawDeadline) {
        this.taskTitle = taskTitle;
        this.formattedDeadline = formattedDeadline;
        this.projectName = projectName;
        this.rawDeadline = rawDeadline;
    }

    public Date getRawDeadline() {
        return rawDeadline;
    }

    public String getTaskTitle() { return taskTitle; }
    public String getDeadline() { return deadline; }
    public String getProjectName() { return projectName; }
}
