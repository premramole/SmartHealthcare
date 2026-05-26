package com.example.smarthealthcare.models;

public class HealthRecord {
    private String recordId;
    private String patientId;
    private int heartRate;
    private String bloodPressure;
    private float temperature;
    private float oxygenLevel;
    private String recordedTime;
    
    public HealthRecord() {
        // Default constructor required for Firebase
    }
    
    public HealthRecord(String recordId, String patientId, int heartRate, String bloodPressure, 
                       float temperature, float oxygenLevel, String recordedTime) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.oxygenLevel = oxygenLevel;
        this.recordedTime = recordedTime;
    }
    
    // Getters and setters
    public String getRecordId() {
        return recordId;
    }
    
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public int getHeartRate() {
        return heartRate;
    }
    
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
    
    public String getBloodPressure() {
        return bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    public float getOxygenLevel() {
        return oxygenLevel;
    }
    
    public void setOxygenLevel(float oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }
    
    public String getRecordedTime() {
        return recordedTime;
    }
    
    public void setRecordedTime(String recordedTime) {
        this.recordedTime = recordedTime;
    }
}
