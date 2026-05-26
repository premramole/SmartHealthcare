package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;

public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase database;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Layout inflated successfully");

            initViews();
            initDatabase();
            setListeners();
            
            // Check if user is already logged in
            // checkLoginStatus(); // Commented out so the user explicitly sees the Login screen on launch
            
            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        try {
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            tvRegister = findViewById(R.id.tv_register);
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
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

    private void setListeners() {
        try {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUser();
                }
            });

            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening register activity: ", e);
                        Toast.makeText(LoginActivity.this, "Error opening registration", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }

    private void checkLoginStatus() {
        try {
            String userId = sharedPreferences.getString(KEY_USER_ID, null);
            if (userId != null) {
                Log.d(TAG, "User already logged in: " + userId);
                UserEntity user = userDao.getUserById(userId);
                if (user != null) {
                    navigateToDashboard(user);
                } else {
                    // User not found in database, clear preferences
                    clearLoginPreferences();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status: ", e);
        }
    }

    private void loginUser() {
        try {
            String email = etEmail.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            android.widget.RadioGroup rgRole = findViewById(R.id.rg_role);
            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            String selectedRole = (selectedRoleId == R.id.rb_doctor) ? "doctor" : "patient";

            Log.d(TAG, "Attempting to sign in user: " + email + " as " + selectedRole);
            
            // Authenticate user using Room database
            UserEntity user = userDao.getUserByEmail(email);
            
            if (user != null && user.getPassword().equals(password)) {
                String actualRole = user.getRole();
                if (actualRole == null || actualRole.isEmpty()) {
                    actualRole = "patient";
                }
                
                if (!selectedRole.equalsIgnoreCase(actualRole.trim())) {
                    Toast.makeText(this, "Role mismatch: This account belongs to a " + actualRole.toLowerCase() + ". Please select the correct login option.", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Login successful for user: " + user.getName());
                
                // Save login state
                saveLoginState(user.getUserId());
                
                // Navigate to appropriate dashboard
                navigateToDashboard(user);
                
            } else {
                // Failsafe: If test accounts are missing, create them on the fly
                if (email.equals("doctor@test.com") || email.equals("patient@test.com")) {
                    Log.d(TAG, "Test account login attempt. Creating test account if missing...");
                    createTestAccount(email, password, selectedRole);
                    // Re-fetch user
                    user = userDao.getUserByEmail(email);
                    if (user != null && user.getPassword().equals(password)) {
                        Log.d(TAG, "Login successful after creating test account");
                        saveLoginState(user.getUserId());
                        navigateToDashboard(user);
                        return;
                    }
                }
                
                Log.w(TAG, "Login failed: Invalid email or password");
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loginUser: ", e);
            Toast.makeText(this, "Error during login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createTestAccount(String email, String password, String role) {
        try {
            UserEntity user = new UserEntity();
            if (email.equals("doctor@test.com")) {
                user.setUserId("doctor_test_001");
                user.setName("Dr. Jane Smith");
                user.setEmail("doctor@test.com");
                user.setPassword("password");
                user.setRole("doctor");
                user.setSpecialization("Cardiologist");
                user.setFees(500);
                user.setLocation("Downtown Clinic");
            } else {
                user.setUserId("patient_test_001");
                user.setName("John Doe");
                user.setEmail("patient@test.com");
                user.setPassword("password");
                user.setRole("patient");
            }
            userDao.insertUser(user);
            Log.d(TAG, "Test account created: " + email);
        } catch (Exception e) {
            Log.e(TAG, "Error creating test account", e);
        }
    }

    private void saveLoginState(String userId) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
            Log.d(TAG, "Login state saved for user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error saving login state: ", e);
        }
    }

    private void clearLoginPreferences() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Log.d(TAG, "Login preferences cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing login preferences: ", e);
        }
    }

    private void navigateToDashboard(UserEntity user) {
        try {
            Log.d(TAG, "navigateToDashboard started for user: " + user.getName());
            
            String role = user.getRole();
            Log.d(TAG, "User role: " + role);
            
            // Check if role is null or empty
            if (role == null || role.isEmpty()) {
                Log.w(TAG, "User role is null or empty, defaulting to patient");
                role = "patient";
            }

            Intent intent;
            if ("doctor".equalsIgnoreCase(role.trim())) {
                Log.d(TAG, "Navigating to DoctorDashboardActivity");
                intent = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
            } else {
                Log.d(TAG, "Navigating to PatientDashboardActivity");
                intent = new Intent(LoginActivity.this, PatientDashboardActivity.class);
            }

            // Pass user data to dashboard
            intent.putExtra("user_id", user.getUserId());
            intent.putExtra("user_name", user.getName());
            intent.putExtra("user_email", user.getEmail());
            intent.putExtra("user_role", user.getRole());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in navigateToDashboard: ", e);
            Toast.makeText(this, "Error during navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}