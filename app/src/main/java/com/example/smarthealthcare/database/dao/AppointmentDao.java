package com.example.smarthealthcare.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smarthealthcare.database.entities.AppointmentEntity;

import java.util.List;

@Dao
public interface AppointmentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAppointment(AppointmentEntity appointment);
    
    @Update
    int updateAppointment(AppointmentEntity appointment);
    
    @Query("UPDATE appointments SET status = :status WHERE appointmentId = :appointmentId")
    int updateAppointmentStatus(String appointmentId, String status);
    
    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY appointmentDate DESC")
    LiveData<List<AppointmentEntity>> getAppointmentsForPatient(String patientId);
    
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY appointmentDate DESC")
    LiveData<List<AppointmentEntity>> getAppointmentsForDoctor(String doctorId);
    
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND status = :status ORDER BY appointmentDate DESC")
    LiveData<List<AppointmentEntity>> getDoctorAppointmentsByStatus(String doctorId, String status);
    
    @Query("SELECT * FROM appointments WHERE patientId = :patientId AND status = :status ORDER BY appointmentDate DESC")
    LiveData<List<AppointmentEntity>> getPatientAppointmentsByStatus(String patientId, String status);
    
    @Query("DELETE FROM appointments WHERE appointmentId = :appointmentId")
    int deleteAppointment(String appointmentId);
    
    @Query("SELECT * FROM appointments ORDER BY appointmentDate DESC")
    LiveData<List<AppointmentEntity>> getAllAppointments();

    @Query("UPDATE appointments SET patientRating = :rating, isRated = 1 WHERE appointmentId = :appointmentId")
    void rateAppointment(String appointmentId, float rating);
}
