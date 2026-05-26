package com.example.smarthealthcare.models;

public class Report {
    private String reportId;
    private String userId;
    private String fileName;
    private String fileUrl;
    private long uploadTimestamp;
    
    public Report() {
        // Default constructor required for Firebase
    }
    
    public Report(String reportId, String userId, String fileName, String fileUrl, long uploadTimestamp) {
        this.reportId = reportId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploadTimestamp = uploadTimestamp;
    }
    
    // Getters and setters
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public long getUploadTimestamp() {
        return uploadTimestamp;
    }
    
    public void setUploadTimestamp(long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
}