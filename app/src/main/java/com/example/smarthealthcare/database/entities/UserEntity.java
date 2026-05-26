package com.example.smarthealthcare.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "users")
public class UserEntity {
    
    @PrimaryKey
    @NonNull
    private String userId;
    private String name;
    private String email;
    private String password;
    private String role;
    private String medicalHistory;
    private String specialization;
    private double rating;
    private String location;
    private int fees;
    private String availableDays;  // e.g. "Mon,Tue,Wed"
    private String timeSlots;      // e.g. "9:00 AM - 11:00 AM, 3:00 PM - 5:00 PM"
    private double latitude;
    private double longitude;
    private long createdAt;
    
    public UserEntity() {
    }
    
    @Ignore
    public UserEntity(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        if ("doctor".equals(role) && name != null) {
            String trimmed = name.trim();
            if (trimmed.toLowerCase().startsWith("dr. ")) {
                return "Dr. " + trimmed.substring(4).trim();
            } else if (trimmed.toLowerCase().startsWith("dr.")) {
                return "Dr. " + trimmed.substring(3).trim();
            } else if (trimmed.toLowerCase().startsWith("dr ")) {
                return "Dr. " + trimmed.substring(3).trim();
            }
            return "Dr. " + trimmed;
        }
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getFees() {
        return fees;
    }
    
    public void setFees(int fees) {
        this.fees = fees;
    }
    
    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
