package com.example.smarthealthcare.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smarthealthcare.database.entities.MedicalReportEntity;

import java.util.List;

@Dao
public interface MedicalReportDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReport(MedicalReportEntity report);
    
    @Query("SELECT * FROM medical_reports WHERE patientId = :patientId ORDER BY uploadDate DESC")
    LiveData<List<MedicalReportEntity>> getReportsForPatient(String patientId);
    
    @Query("SELECT * FROM medical_reports WHERE reportId = :reportId")
    MedicalReportEntity getReportById(String reportId);
    
    @Query("DELETE FROM medical_reports WHERE reportId = :reportId")
    int deleteReport(String reportId);
    
    @Query("SELECT * FROM medical_reports ORDER BY uploadDate DESC")
    LiveData<List<MedicalReportEntity>> getAllReports();
}
