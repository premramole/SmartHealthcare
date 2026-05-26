package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;
import com.example.smarthealthcare.database.entities.UserEntity;

import java.util.List;

public class DoctorDashboardActivity extends AppCompatActivity {
    
    private static final String TAG = "DoctorDashboard";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";
    
    private Button btnAppointments, btnMessages, btnSchedule, btnHistory;
    private ImageButton btnEarnings;
    private TextView tvEarnings, tvAppointmentsCount, tvWelcome, tvSpecialization;
    
    private AppDatabase database;
    private UserDao userDao;
    private AppointmentDao appointmentDao;
    private SharedPreferences sharedPreferences;
    private UserEntity currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        try {
            initDatabase();
            initViews();
            loadUserData();
            setListeners();

            Log.d(TAG, "DoctorDashboardActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        userDao = database.userDao();
        appointmentDao = database.appointmentDao();
    }

    private void loadUserData() {
        try {
            // Get user ID from intent or preferences
            String userId = getIntent().getStringExtra("user_id");
            if (userId == null) {
                userId = sharedPreferences.getString(KEY_USER_ID, null);
            }

            if (userId != null) {
                currentUser = userDao.getUserById(userId);
                if (currentUser != null && currentUser.getRole() != null && "doctor".equalsIgnoreCase(currentUser.getRole().trim())) {
                    // Update welcome message
                    if (tvWelcome != null) {
                        tvWelcome.setText("Welcome, " + currentUser.getName() + "!");
                    }
                    if (tvSpecialization != null) {
                        tvSpecialization.setText(currentUser.getSpecialization());
                    }

                    // Load dashboard data
                    loadDashboardData();

                    Log.d(TAG, "Doctor data loaded: " + currentUser.getName());
                } else {
                    Log.w(TAG, "User not found or not a doctor");
                    Toast.makeText(this, "Access denied. Doctors only.", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                }
            } else {
                Log.w(TAG, "No user ID found");
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: ", e);
        }
    }

    private void initViews() {
        try {
            btnAppointments = findViewById(R.id.btn_appointments);
            btnMessages = findViewById(R.id.btn_messages);
            btnEarnings = findViewById(R.id.btn_earnings);
            btnSchedule = findViewById(R.id.btn_schedule);
            btnHistory = findViewById(R.id.btn_history);
            tvEarnings = findViewById(R.id.tv_earnings);
            tvAppointmentsCount = findViewById(R.id.tv_appointments_count);
            tvWelcome = findViewById(R.id.tv_welcome);
            tvSpecialization = findViewById(R.id.tv_specialization);

            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }

    private void loadDashboardData() {
        try {
            if (currentUser == null) return;

            // Load pending appointments count
            appointmentDao.getDoctorAppointmentsByStatus(currentUser.getUserId(), "pending")
                    .observe(this, new Observer<List<AppointmentEntity>>() {
                        @Override
                        public void onChanged(List<AppointmentEntity> appointments) {
                            int pendingCount = appointments != null ? appointments.size() : 0;
                            tvAppointmentsCount.setText(String.valueOf(pendingCount));
                            Log.d(TAG, "Pending appointments: " + pendingCount);
                        }
                    });

            // Calculate total earnings (completed appointments * fees)
            appointmentDao.getDoctorAppointmentsByStatus(currentUser.getUserId(), "completed")
                    .observe(this, new Observer<List<AppointmentEntity>>() {
                        @Override
                        public void onChanged(List<AppointmentEntity> appointments) {
                            int totalEarnings = 0;
                            if (appointments != null) {
                                for (AppointmentEntity appointment : appointments) {
                                    totalEarnings += currentUser.getFees();
                                }
                            }
                            tvEarnings.setText("₹" + totalEarnings);
                            Log.d(TAG, "Total earnings: " + totalEarnings);
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data: ", e);
        }
    }

    private void setListeners() {
        try {
            Log.d(TAG, "Setting up listeners");

            btnAppointments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d(TAG, "Appointments button clicked");
                        Intent intent = new Intent(DoctorDashboardActivity.this, DoctorAppointmentsActivity.class);
                        intent.putExtra("user_id", currentUser.getUserId());
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening appointments: ", e);
                        Toast.makeText(DoctorDashboardActivity.this, "Error opening appointments", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnMessages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d(TAG, "Messages button clicked");
                        Intent intent = new Intent(DoctorDashboardActivity.this, ChatListActivity.class);
                        intent.putExtra("user_id", currentUser.getUserId());
                        intent.putExtra("user_name", currentUser.getName());
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening messages: ", e);
                        Toast.makeText(DoctorDashboardActivity.this, "Error opening messages", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnEarnings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d(TAG, "Refresh earnings button clicked");
                        loadDashboardData();
                        Toast.makeText(DoctorDashboardActivity.this, "Data refreshed", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error refreshing data: ", e);
                        Toast.makeText(DoctorDashboardActivity.this, "Error refreshing data", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            btnSchedule.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Schedule button clicked");
                    Intent intent = new Intent(DoctorDashboardActivity.this, DoctorScheduleActivity.class);
                    intent.putExtra("user_id", currentUser.getUserId());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening schedule: ", e);
                    Toast.makeText(DoctorDashboardActivity.this, "Error opening schedule", Toast.LENGTH_SHORT).show();
                }
            });

            btnHistory.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "History button clicked");
                    Intent intent = new Intent(DoctorDashboardActivity.this, DoctorHistoryActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening history: ", e);
                    Toast.makeText(DoctorDashboardActivity.this, "Error opening history", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d(TAG, "Listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_doctor_dashboard, menu);
            Log.d(TAG, "Menu created");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating menu: ", e);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.action_profile) {
                Log.d(TAG, "Profile menu item clicked");
                Intent intent = new Intent(DoctorDashboardActivity.this, DoctorProfileActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.action_logout) {
                Log.d(TAG, "Logout menu item clicked");
                logout();
                return true;
            }

            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            Log.e(TAG, "Error handling menu item: ", e);
            return false;
        }
    }

    private void logout() {
        try {
            Log.d(TAG, "Logging out");
            // Clear login preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            redirectToLogin();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: ", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToLogin() {
        try {
            Log.d(TAG, "Redirecting to login");
            Intent intent = new Intent(DoctorDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to login: ", e);
        }
    }
}