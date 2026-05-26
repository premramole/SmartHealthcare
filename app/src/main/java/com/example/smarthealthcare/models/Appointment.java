package com.example.smarthealthcare.models;

public class Appointment {
    private String appointmentId;
    private String doctorId;
    private String patientId;
    private String patientName; // New field to store patient name
    private String dateTime;
    private String mode; // online or offline
    private String status; // pending, confirmed, rejected, completed
    
    public Appointment() {
        // Default constructor required for Firebase
    }
    
    public Appointment(String appointmentId, String doctorId, String patientId, String dateTime, String mode, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.dateTime = dateTime;
        this.mode = mode;
        this.status = status;
    }
    
    // Getters and setters
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
    
    public String getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
}