package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private static final String TAG = "SplashActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        insertSampleDoctors();
        
        // Navigate to main activity after splash duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
    
    private void insertSampleDoctors() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(this);
                UserDao userDao = db.userDao();
                
                // Add first sample doctor
                if (userDao.getUserByEmail("rahul@doc.com") == null) {
                    UserEntity doctor1 = new UserEntity(UUID.randomUUID().toString(), "Dr. Rahul Mishra", "rahul@doc.com", "password123", "doctor");
                    doctor1.setSpecialization("Orthopedics");
                    doctor1.setLocation("Pune");
                    doctor1.setRating(4.8);
                    doctor1.setFees(1000);
                    userDao.insertUser(doctor1);
                    Log.d(TAG, "Sample Doctor 1 added");
                }
                
                // Add second sample doctor
                if (userDao.getUserByEmail("vijay@doc.com") == null) {
                    UserEntity doctor2 = new UserEntity(UUID.randomUUID().toString(), "Dr. Vijay Patil", "vijay@doc.com", "password123", "doctor");
                    doctor2.setSpecialization("Neurology");
                    doctor2.setLocation("Mumbai");
                    doctor2.setRating(4.9);
                    doctor2.setFees(1500);
                    userDao.insertUser(doctor2);
                    Log.d(TAG, "Sample Doctor 2 added");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed inserting sample doctors", e);
            }
        }).start();
    }
}