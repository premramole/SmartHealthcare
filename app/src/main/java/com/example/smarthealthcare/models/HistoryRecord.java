package com.example.smarthealthcare.models;

public class HistoryRecord {
    public enum Type {
        APPOINTMENT,
        PRESCRIPTION
    }

    private String id;
    private Type type;
    private long timestamp;
    private String date;
    private String title;
    private String doctorName;
    private String summary;

    public HistoryRecord(String id, Type type, long timestamp, String date, String title, String doctorName, String summary) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.date = date;
        this.title = title;
        this.doctorName = doctorName;
        this.summary = summary;
    }

    // Getters
    public String getId() { return id; }
    public Type getType() { return type; }
    public long getTimestamp() { return timestamp; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDoctorName() { return doctorName; }
    public String getSummary() { return summary; }
}
