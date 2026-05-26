package com.example.smarthealthcare.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "prescriptions")
public class PrescriptionEntity {

    @PrimaryKey
    @NonNull
    private String prescriptionId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String medicines;
    private String dosage;
    private String instructions;
    private String date;
    private long timestamp;

    public PrescriptionEntity() {
    }

    @Ignore
    public PrescriptionEntity(@NonNull String prescriptionId, String appointmentId, String patientId, String doctorId, 
                              String diagnosis, String medicines, String dosage, String instructions, String date) {
        this.prescriptionId = prescriptionId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.medicines = medicines;
        this.dosage = dosage;
        this.instructions = instructions;
        this.date = date;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(@NonNull String prescriptionId) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
