package com.example.smarthealthcare.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smarthealthcare.database.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);
    
    @Update
    int updateUser(UserEntity user);
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserById(String userId);
    
    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    UserEntity login(String email, String password);
    
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    LiveData<List<UserEntity>> getUsersByRole(String role);
    
    @Query("SELECT * FROM users WHERE role = 'doctor' ORDER BY name ASC")
    LiveData<List<UserEntity>> getAllDoctors();
    
    @Query("DELETE FROM users WHERE userId = :userId")
    int deleteUser(String userId);
    
    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("UPDATE users SET fees = :fees, availableDays = :days, timeSlots = :slots WHERE userId = :userId")
    void updateDoctorSchedule(String userId, int fees, String days, String slots);

    @Query("UPDATE users SET rating = :rating WHERE userId = :userId")
    void updateDoctorRating(String userId, double rating);

    @Query("SELECT AVG(patientRating) FROM appointments WHERE doctorId = :doctorId AND isRated = 1")
    double getAverageDoctorRating(String doctorId);
}
