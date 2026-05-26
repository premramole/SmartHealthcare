package com.example.smarthealthcare.models;

public class MedicalReport {
    private String reportId;
    private String patientId;
    private String reportFilePath;
    private String uploadDate;
    
    public MedicalReport() {
        // Default constructor required for Firebase
    }
    
    public MedicalReport(String reportId, String patientId, String reportFilePath, String uploadDate) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.reportFilePath = reportFilePath;
        this.uploadDate = uploadDate;
    }
    
    // Getters and setters
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getReportFilePath() {
        return reportFilePath;
    }
    
    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
    
    public String getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
