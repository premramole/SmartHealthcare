package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.AppointmentAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;
import com.example.smarthealthcare.database.entities.UserEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AppointmentActivity extends AppCompatActivity {
    
    private static final String TAG = "AppointmentActivity";
    
    private Spinner spDoctors, spMode;
    private TextView tvSelectedDate;
    private Button btnSelectDate, btnBookAppointment;
    private RecyclerView rvAppointments;
    
    private AppDatabase database;
    private UserDao userDao;
    private AppointmentDao appointmentDao;
    private String userId, userRole;
    private List<UserEntity> doctorsList;
    private List<AppointmentEntity> appointmentsList;
    private AppointmentAdapter appointmentAdapter;
    private String selectedDate = "";
    private UserEntity selectedDoctor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        
        getIntentData();
        initDatabase();
        initViews();
        setListeners();
        
        if ("patient".equals(userRole)) {
            loadPatientAppointments();
        } else {
            loadDoctorAppointments();
        }
        
        loadDoctors();
    }
    
    private void getIntentData() {
        try {
            userId = getIntent().getStringExtra("user_id");
            userRole = getIntent().getStringExtra("user_role");
            
            if (userId == null || userRole == null) {
                Log.w(TAG, "Missing user data in intent");
                Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d(TAG, "User ID: " + userId + ", Role: " + userRole);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data: ", e);
        }
    }
    
    private void initDatabase() {
        try {
            database = AppDatabase.getDatabase(this);
            userDao = database.userDao();
            appointmentDao = database.appointmentDao();
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }
    
    private void initViews() {
        try {
            spDoctors = findViewById(R.id.sp_doctors);
            spMode = findViewById(R.id.sp_mode);
            tvSelectedDate = findViewById(R.id.tv_selected_date);
            btnSelectDate = findViewById(R.id.btn_select_date);
            btnBookAppointment = findViewById(R.id.btn_book_appointment);
            rvAppointments = findViewById(R.id.rv_appointments);
            
            // Setup mode spinner
            String[] modes = {"Online", "Offline"};
            ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMode.setAdapter(modeAdapter);
            
            // Setup RecyclerView
            rvAppointments.setLayoutManager(new LinearLayoutManager(this));
            doctorsList = new ArrayList<>();
            appointmentsList = new ArrayList<>();
            appointmentAdapter = new AppointmentAdapter(this, appointmentsList, userRole);
            rvAppointments.setAdapter(appointmentAdapter);
            
            // Hide booking controls unconditionally, as booking is handled in DoctorsListActivity
            findViewById(R.id.booking_controls_layout).setVisibility(View.GONE);
            
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }
    
    private void setListeners() {
        try {
            btnSelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    int year = calendar.get(java.util.Calendar.YEAR);
                    int month = calendar.get(java.util.Calendar.MONTH);
                    int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                    
                    android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                        AppointmentActivity.this,
                        (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                            java.util.Calendar timeCalendar = java.util.Calendar.getInstance();
                            int hour = timeCalendar.get(java.util.Calendar.HOUR_OF_DAY);
                            int minute = timeCalendar.get(java.util.Calendar.MINUTE);
                            
                            android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(
                                AppointmentActivity.this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                    java.util.Calendar finalCal = java.util.Calendar.getInstance();
                                    finalCal.set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHour, selectedMinute);
                                    selectedDate = sdf.format(finalCal.getTime());
                                    tvSelectedDate.setText("Selected: " + selectedDate);
                                    Toast.makeText(AppointmentActivity.this, "Date and time selected", Toast.LENGTH_SHORT).show();
                                },
                                hour, minute, true
                            );
                            timePickerDialog.show();
                        },
                        year, month, day
                    );
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Disallow past dates
                    datePickerDialog.show();
                }
            });
            
            btnBookAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookAppointment();
                }
            });
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }
    
    private void loadDoctors() {
        try {
            // Load doctors using Room database
            userDao.getAllDoctors().observe(this, new androidx.lifecycle.Observer<List<UserEntity>>() {
                @Override
                public void onChanged(List<UserEntity> doctors) {
                    if (doctors != null && !doctors.isEmpty()) {
                        doctorsList.clear();
                        doctorsList.addAll(doctors);
                        
                        String[] doctorNames = new String[doctors.size()];
                        for (int i = 0; i < doctors.size(); i++) {
                            UserEntity doctor = doctors.get(i);
                            doctorNames[i] = doctor.getName() + " - " + doctor.getSpecialization();
                        }
                        
                        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(AppointmentActivity.this, android.R.layout.simple_spinner_item, doctorNames);
                        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDoctors.setAdapter(doctorAdapter);
                        
                        spDoctors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedDoctor = doctorsList.get(position);
                                Log.d(TAG, "Selected doctor: " + selectedDoctor.getName());
                            }
                            
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedDoctor = null;
                            }
                        });
                        
                        Log.d(TAG, "Doctors loaded: " + doctors.size());
                    } else {
                        Log.w(TAG, "No doctors found");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading doctors: ", e);
            Toast.makeText(this, "Error loading doctors", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void bookAppointment() {
        try {
            if (selectedDoctor == null) {
                Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create appointment
            String appointmentId = "APT_" + UUID.randomUUID().toString();
            String mode = spMode.getSelectedItem().toString().toLowerCase();
            
            AppointmentEntity appointment = new AppointmentEntity(appointmentId, selectedDoctor.getUserId(), 
                    userId, selectedDoctor.getName(), selectedDate, mode, "pending");
            
            // Insert appointment using Room
            new Thread(() -> {
                appointmentDao.insertAppointment(appointment);
                runOnUiThread(() -> {
                    Log.d(TAG, "Appointment booked successfully");
                    Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Refresh appointments list
                    loadPatientAppointments();
                    
                    // Clear form
                    clearBookingForm();
                });
            }).start();
            
        } catch (Exception e) {
            Log.e(TAG, "Error booking appointment: ", e);
            Toast.makeText(this, "Error booking appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadPatientAppointments() {
        try {
            // Load patient appointments using Room
            appointmentDao.getAppointmentsForPatient(userId).observe(this, new androidx.lifecycle.Observer<List<AppointmentEntity>>() {
                @Override
                public void onChanged(List<AppointmentEntity> appointments) {
                    appointmentsList.clear();
                    if (appointments != null) {
                        appointmentsList.addAll(appointments);
                    }
                    appointmentAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Patient appointments loaded: " + appointmentsList.size());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading patient appointments: ", e);
        }
    }
    
    private void loadDoctorAppointments() {
        try {
            // Load doctor appointments using Room
            appointmentDao.getAppointmentsForDoctor(userId).observe(this, new androidx.lifecycle.Observer<List<AppointmentEntity>>() {
                @Override
                public void onChanged(List<AppointmentEntity> appointments) {
                    appointmentsList.clear();
                    if (appointments != null) {
                        appointmentsList.addAll(appointments);
                    }
                    appointmentAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Doctor appointments loaded: " + appointmentsList.size());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading doctor appointments: ", e);
        }
    }
    
    private void clearBookingForm() {
        try {
            selectedDate = "";
            tvSelectedDate.setText("No date selected");
            spDoctors.setSelection(0);
            spMode.setSelection(0);
            Log.d(TAG, "Booking form cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing booking form: ", e);
        }
    }
    
    public void confirmAppointment(String appointmentId) {
        try {
            // Update appointment status using Room
            new Thread(() -> {
                appointmentDao.updateAppointmentStatus(appointmentId, "confirmed");
                runOnUiThread(() -> {
                    Log.d(TAG, "Appointment confirmed");
                    Toast.makeText(this, "Appointment confirmed", Toast.LENGTH_SHORT).show();
                    
                    // Refresh list
                    loadDoctorAppointments();
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error confirming appointment: ", e);
            Toast.makeText(this, "Error confirming appointment", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelAppointment(String appointmentId) {
        try {
            new Thread(() -> {
                appointmentDao.updateAppointmentStatus(appointmentId, "canceled");
                runOnUiThread(() -> {
                    Log.d(TAG, "Appointment canceled");
                    Toast.makeText(this, "Appointment canceled", Toast.LENGTH_SHORT).show();
                    
                    if ("patient".equals(userRole)) {
                        // Refresh manually or let LiveData handle it (it will auto-update)
                    } else {
                        // Refresh manually
                    }
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error canceling appointment: ", e);
            Toast.makeText(this, "Error canceling appointment", Toast.LENGTH_SHORT).show();
        }
    }

    public void completeAppointment(String appointmentId) {
        try {
            new Thread(() -> {
                appointmentDao.updateAppointmentStatus(appointmentId, "completed");
                runOnUiThread(() -> {
                    Log.d(TAG, "Appointment completed");
                    Toast.makeText(this, "Appointment marked as completed!", Toast.LENGTH_SHORT).show();
                });
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error completing appointment: ", e);
            Toast.makeText(this, "Error completing appointment", Toast.LENGTH_SHORT).show();
        }
    }
}
