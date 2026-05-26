package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;

public class PatientDashboardActivity extends AppCompatActivity {

    private static final String TAG = "PatientDashboard";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";
    
    private Button btnDoctors, btnAppointments, btnReports, btnMessages, btnChatbot, btnVideoCall, btnHealthHistory;
    private TextView tvWelcome;
    private AppDatabase database;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private UserEntity currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        initDatabase();
        initViews();
        loadUserData();
        setListeners();
    }

    private void initDatabase() {
        try {
            database = AppDatabase.getDatabase(this);
            userDao = database.userDao();
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }
    
    private void loadUserData() {
        try {
            // Get user ID from intent or preferences
            String userId = getIntent().getStringExtra("user_id");
            if (userId == null) {
                userId = sharedPreferences.getString(KEY_USER_ID, null);
            }
            
            if (userId != null) {
                // Now uses Room instead of legacy DatabaseHelper
                currentUser = userDao.getUserById(userId);
                
                if (currentUser != null) {
                    // Personalized greeting: Extract first name
                    String fullName = currentUser.getName();
                    String firstName = (fullName != null && fullName.contains(" ")) 
                                        ? fullName.split(" ")[0] 
                                        : fullName;
                    
                    if (tvWelcome != null) {
                        tvWelcome.setText("Hello, " + (firstName != null ? firstName : "There") + "!");
                    }
                    Log.d(TAG, "User data loaded: " + currentUser.getName());
                } else {
                    Log.w(TAG, "User not found in database. Forcing logout.");
                    Toast.makeText(this, "Session invalid, please login again.", Toast.LENGTH_SHORT).show();
                    logout();
                }
            } else {
                Log.w(TAG, "No user ID found");
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                logout();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: ", e);
        }
    }

    private void initViews() {
        try {
            btnDoctors = findViewById(R.id.btn_doctors);
            btnAppointments = findViewById(R.id.btn_appointments);
            btnReports = findViewById(R.id.btn_reports);
            btnMessages = findViewById(R.id.btn_messages);
            tvWelcome = findViewById(R.id.tv_welcome);
            
            // Optional buttons that may not exist in all layouts
            btnChatbot = findViewById(R.id.btn_chatbot);
            btnVideoCall = findViewById(R.id.btn_video_call);
            btnHealthHistory = findViewById(R.id.btn_health_history);
            
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }

    private void setListeners() {
        try {
            // Doctors button
            if (btnDoctors != null) {
                btnDoctors.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (currentUser == null) return;
                            Intent intent = new Intent(PatientDashboardActivity.this, DoctorsListActivity.class);
                            intent.putExtra("user_id", currentUser.getUserId());
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error opening doctors list activity: ", e);
                            Toast.makeText(PatientDashboardActivity.this, "Error opening doctors list", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Appointments button
            if (btnAppointments != null) {
                btnAppointments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (currentUser == null) return;
                            Intent intent = new Intent(PatientDashboardActivity.this, AppointmentActivity.class);
                            intent.putExtra("user_id", currentUser.getUserId());
                            intent.putExtra("user_role", currentUser.getRole());
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error opening appointments activity: ", e);
                            Toast.makeText(PatientDashboardActivity.this, "Error opening appointments", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }



            // Reports button
            if (btnReports != null) {
                btnReports.setOnClickListener(v -> {
                    try {
                        if (currentUser == null) return;
                        Intent intent = new Intent(PatientDashboardActivity.this, ReportUploadActivity.class);
                        intent.putExtra("user_id", currentUser.getUserId());
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening reports activity: ", e);
                        Toast.makeText(PatientDashboardActivity.this, "Error opening reports", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // AI Chatbot button
            if (btnChatbot != null) {
                btnChatbot.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(PatientDashboardActivity.this, ChatbotActivity.class));
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening chatbot activity: ", e);
                        Toast.makeText(PatientDashboardActivity.this, "Error opening AI Chatbot", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Prescriptions button
            Button btnPrescriptions = findViewById(R.id.btn_prescriptions);
            if (btnPrescriptions != null) {
                btnPrescriptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (currentUser == null) return;
                            Intent intent = new Intent(PatientDashboardActivity.this, PatientPrescriptionsActivity.class);
                            intent.putExtra("user_id", currentUser.getUserId());
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error opening prescriptions activity: ", e);
                            Toast.makeText(PatientDashboardActivity.this, "Error opening prescriptions", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Messages button
            if (btnMessages != null) {
                btnMessages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (currentUser == null) return;
                            Intent intent = new Intent(PatientDashboardActivity.this, ChatListActivity.class);
                            intent.putExtra("user_id", currentUser.getUserId());
                            intent.putExtra("user_name", currentUser.getName());
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error opening chat list activity: ", e);
                            Toast.makeText(PatientDashboardActivity.this, "Error opening messages", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


            // Health History button
            if (btnHealthHistory != null) {
                btnHealthHistory.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(PatientDashboardActivity.this, HealthHistoryActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening health history activity: ", e);
                        Toast.makeText(PatientDashboardActivity.this, "Error opening health history", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            if (currentUser != null) {
                Intent intent = new Intent(PatientDashboardActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", currentUser.getUserId());
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        // Clear login preferences
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
        
        Intent intent = new Intent(PatientDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}