package com.example.employee_gamification;

import com.google.firebase.Timestamp;

public class RecentTaskModel {
    private String task;
    private Timestamp deadline;

    public RecentTaskModel() {}

    public RecentTaskModel(String task, Timestamp deadline) {
        this.task = task;
        this.deadline = deadline;
    }

    public String getTask() {
        return task;
    }

    public Timestamp getDeadline() {
        return deadline;
    }
}

