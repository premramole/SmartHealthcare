package com.example.smarthealthcare.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.Appointment;
import com.example.smarthealthcare.adapters.DoctorAppointmentAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsActivity extends AppCompatActivity {
    
    private static final String TAG = "DoctorAppointments";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";

    private RecyclerView recyclerView;
    private DoctorAppointmentAdapter appointmentAdapter;
    private List<Appointment> appointments;
    
    private AppDatabase database;
    private AppointmentDao appointmentDao;
    private SharedPreferences sharedPreferences;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_doctor_appointments);
            Log.d(TAG, "Layout inflated successfully");

            initDatabase();
            initViews();
            loadAppointments();
            
            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing appointments screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initDatabase() {
        try {
            database = AppDatabase.getDatabase(this);
            appointmentDao = database.appointmentDao();
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            
            currentUserId = getIntent().getStringExtra("user_id");
            if (currentUserId == null) {
                currentUserId = sharedPreferences.getString(KEY_USER_ID, null);
            }
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }

    private void initViews() {
        try {
            recyclerView = findViewById(R.id.recycler_view_appointments);
            appointments = new ArrayList<>();
            appointmentAdapter = new DoctorAppointmentAdapter(appointments);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(appointmentAdapter);
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }

    private void loadAppointments() {
        try {
            Log.d(TAG, "Loading appointments");
            
            if (currentUserId == null) {
                Log.w(TAG, "User not authenticated");
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Loading appointments for doctor ID: " + currentUserId);
            
            appointmentDao.getAppointmentsForDoctor(currentUserId).observe(this, new Observer<List<AppointmentEntity>>() {
                @Override
                public void onChanged(List<AppointmentEntity> appointmentEntities) {
                    appointments.clear();
                    if (appointmentEntities != null) {
                        for (AppointmentEntity entity : appointmentEntities) {
                            Appointment appointment = new Appointment(
                                entity.getAppointmentId(),
                                entity.getDoctorId(),
                                entity.getPatientId(),
                                entity.getAppointmentDate(),
                                entity.getMode(),
                                entity.getStatus()
                            );
                            appointment.setPatientName(entity.getPatientName());
                            appointments.add(appointment);
                        }
                    }
                    appointmentAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Appointments loaded: " + appointments.size());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadAppointments: ", e);
            Toast.makeText(this, "Error loading appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}