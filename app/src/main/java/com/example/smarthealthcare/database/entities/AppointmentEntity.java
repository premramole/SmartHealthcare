package com.example.smarthealthcare.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "appointments")
public class AppointmentEntity {
    
    @PrimaryKey
    @NonNull
    private String appointmentId;
    private String doctorId;
    private String patientId;
    private String patientName;
    private String appointmentDate;
    private String mode;
    private String status;
    private String symptoms;
    private float patientRating;  // 0 = not yet rated, 1-5 = star rating
    private int isRated;          // 0 = not rated, 1 = rated
    private long createdAt;
    
    public AppointmentEntity() {
    }
    
    @Ignore
    public AppointmentEntity(String appointmentId, String doctorId, String patientId, 
                         String patientName, String appointmentDate, String mode, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentDate = appointmentDate;
        this.mode = mode;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public float getPatientRating() {
        return patientRating;
    }

    public void setPatientRating(float patientRating) {
        this.patientRating = patientRating;
    }

    public int getIsRated() {
        return isRated;
    }

    public void setIsRated(int isRated) {
        this.isRated = isRated;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
