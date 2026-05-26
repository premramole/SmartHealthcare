package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.DatabaseHelper;
import com.example.smarthealthcare.models.HealthRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthRecordActivity extends AppCompatActivity {
    
    private static final String TAG = "HealthRecordActivity";
    
    private EditText etHeartRate, etBloodPressure, etTemperature, etOxygenLevel;
    private Button btnSaveRecord, btnViewRecords;
    private DatabaseHelper databaseHelper;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);
        
        try {
            initViews();
            initDatabase();
            getIntentData();
            setListeners();
            
            Log.d(TAG, "HealthRecordActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing health record screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        try {
            etHeartRate = findViewById(R.id.et_heart_rate);
            etBloodPressure = findViewById(R.id.et_blood_pressure);
            etTemperature = findViewById(R.id.et_temperature);
            etOxygenLevel = findViewById(R.id.et_oxygen_level);
            btnSaveRecord = findViewById(R.id.btn_save_record);
            btnViewRecords = findViewById(R.id.btn_view_records);
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }
    
    private void initDatabase() {
        try {
            databaseHelper = DatabaseHelper.getInstance(this);
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }
    
    private void getIntentData() {
        try {
            patientId = getIntent().getStringExtra("user_id");
            if (patientId == null) {
                Log.w(TAG, "No patient ID provided in intent");
                Toast.makeText(this, "Error: Patient ID not found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d(TAG, "Patient ID: " + patientId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data: ", e);
        }
    }
    
    private void setListeners() {
        try {
            btnSaveRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveHealthRecord();
                }
            });
            
            btnViewRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Navigate to view records activity
                    Toast.makeText(HealthRecordActivity.this, "View records feature coming soon", Toast.LENGTH_SHORT).show();
                }
            });
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }
    
    private void saveHealthRecord() {
        try {
            String heartRateStr = etHeartRate.getText().toString().trim();
            String bloodPressure = etBloodPressure.getText().toString().trim();
            String temperatureStr = etTemperature.getText().toString().trim();
            String oxygenLevelStr = etOxygenLevel.getText().toString().trim();
            
            if (heartRateStr.isEmpty() || bloodPressure.isEmpty() || 
                temperatureStr.isEmpty() || oxygenLevelStr.isEmpty()) {
                Toast.makeText(this, "Please fill all health metrics", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int heartRate = Integer.parseInt(heartRateStr);
            float temperature = Float.parseFloat(temperatureStr);
            float oxygenLevel = Float.parseFloat(oxygenLevelStr);
            
            // Validate ranges
            if (heartRate < 30 || heartRate > 200) {
                Toast.makeText(this, "Heart rate should be between 30-200 bpm", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (temperature < 35.0f || temperature > 42.0f) {
                Toast.makeText(this, "Temperature should be between 35.0-42.0°C", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (oxygenLevel < 70 || oxygenLevel > 100) {
                Toast.makeText(this, "Oxygen level should be between 70-100%", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get current timestamp
            String recordedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            
            // Insert health record into database
            long result = databaseHelper.insertHealthRecord(patientId, heartRate, 
                    bloodPressure, temperature, oxygenLevel);
            
            if (result != -1) {
                Log.d(TAG, "Health record saved successfully");
                Toast.makeText(this, "Health record saved successfully!", Toast.LENGTH_SHORT).show();
                
                // Clear the form
                clearForm();
            } else {
                Log.w(TAG, "Failed to save health record");
                Toast.makeText(this, "Failed to save health record. Please try again.", Toast.LENGTH_LONG).show();
            }
            
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format error: ", e);
            Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving health record: ", e);
            Toast.makeText(this, "Error saving health record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void clearForm() {
        try {
            etHeartRate.setText("");
            etBloodPressure.setText("");
            etTemperature.setText("");
            etOxygenLevel.setText("");
            Log.d(TAG, "Form cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing form: ", e);
        }
    }
}
