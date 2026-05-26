package com.example.smarthealthcare.models;

public class Doctor {
    private String doctorId;
    private String name;
    private String specialization;
    private double rating;
    private String location;
    private int fees;
    private double latitude;
    private double longitude;
    
    public Doctor() {
        // Default constructor required for Firebase
    }
    
    public Doctor(String doctorId, String name, String specialization, double rating, String location, int fees, double latitude, double longitude) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
        this.location = location;
        this.fees = fees;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
}