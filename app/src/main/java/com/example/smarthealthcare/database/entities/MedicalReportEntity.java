package com.example.smarthealthcare.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "medical_reports")
public class MedicalReportEntity {
    
    @PrimaryKey
    @NonNull
    private String reportId;
    private String patientId;
    private String reportFilePath;
    private String uploadDate;
    private long createdAt;
    
    public MedicalReportEntity() {
    }
    
    @Ignore
    public MedicalReportEntity(String reportId, String patientId, String reportFilePath, String uploadDate) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.reportFilePath = reportFilePath;
        this.uploadDate = uploadDate;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
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
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
