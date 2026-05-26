package com.example.smarthealthcare.models;

public class Prescription {
    private String prescriptionId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String medicines;
    private String dosage;
    private String instructions;
    private long timestamp;
    private String date;

    public Prescription() {
        // Default constructor required for Firebase
    }

    public Prescription(String prescriptionId, String appointmentId, String patientId, String doctorId,
                       String diagnosis, String medicines, String dosage, String instructions, 
                       long timestamp, String date) {
        this.prescriptionId = prescriptionId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.medicines = medicines;
        this.dosage = dosage;
        this.instructions = instructions;
        this.timestamp = timestamp;
        this.date = date;
    }

    // Getters and setters
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}