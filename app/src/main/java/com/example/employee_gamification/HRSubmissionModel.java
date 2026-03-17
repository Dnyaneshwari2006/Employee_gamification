package com.example.employee_gamification;

public class HRSubmissionModel {
    private String taskName;  // Added field for task name
    private String submittedBy;
    private String submissionTime;
    private String reply;
    private String attachmentUrl;

    // Default constructor required for Firebase
    public HRSubmissionModel() {}

    public HRSubmissionModel(String taskName, String submittedBy, String submissionTime, String reply, String attachmentUrl) {
        this.taskName = taskName;
        this.submittedBy = submittedBy;
        this.submissionTime = submissionTime;
        this.reply = reply;
        this.attachmentUrl = attachmentUrl;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
}
