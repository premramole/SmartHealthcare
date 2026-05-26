package com.example.smarthealthcare.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smarthealthcare.database.entities.PrescriptionEntity;

import java.util.List;

@Dao
public interface PrescriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrescription(PrescriptionEntity prescription);

    @Update
    void updatePrescription(PrescriptionEntity prescription);

    @Delete
    void deletePrescription(PrescriptionEntity prescription);

    @Query("SELECT * FROM prescriptions WHERE prescriptionId = :id")
    PrescriptionEntity getPrescriptionById(String id);

    @Query("SELECT * FROM prescriptions WHERE appointmentId = :appointmentId")
    PrescriptionEntity getPrescriptionByAppointmentId(String appointmentId);

    @Query("SELECT * FROM prescriptions WHERE patientId = :patientId ORDER BY timestamp DESC")
    LiveData<List<PrescriptionEntity>> getPatientPrescriptions(String patientId);
    
    @Query("SELECT * FROM prescriptions WHERE doctorId = :doctorId ORDER BY timestamp DESC")
    LiveData<List<PrescriptionEntity>> getDoctorPrescriptions(String doctorId);
}
