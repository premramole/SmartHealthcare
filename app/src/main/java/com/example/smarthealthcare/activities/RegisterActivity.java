package com.example.smarthealthcare.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    
    private static final String TAG = "RegisterActivity";
    
    private EditText etName, etEmail, etPassword, etConfirmPassword, etSpecialization, etLocation;
    private RadioGroup rgUserRole;
    private RadioButton rbPatient, rbDoctor;
    private Button btnRegister, btnRegSetGps, btnRegSelectMap;
    private LinearLayout layoutDoctorLocation;
    private TextView tvLogin;
    private AppDatabase database;
    private UserDao userDao;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<Intent> mapPickerLauncher;
    private double doctorLat = 0, doctorLng = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_register);
            Log.d(TAG, "Layout inflated successfully");
            
            initMapLauncher();
            initViews();
            initDatabase();
            setListeners();
            updateUIForRole(); // Show/hide doctor-specific fields based on default selection
            
            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing registration screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        try {
            etName = findViewById(R.id.et_name);
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            etConfirmPassword = findViewById(R.id.et_confirm_password);
            etSpecialization = findViewById(R.id.et_specialization);
            etLocation = findViewById(R.id.et_location);
            rgUserRole = findViewById(R.id.rg_user_role);
            rbPatient = findViewById(R.id.rb_patient);
            rbDoctor = findViewById(R.id.rb_doctor);
            btnRegister = findViewById(R.id.btn_register);
            btnRegSetGps = findViewById(R.id.btn_reg_set_gps);
            btnRegSelectMap = findViewById(R.id.btn_reg_select_map);
            layoutDoctorLocation = findViewById(R.id.layout_doctor_location);
            tvLogin = findViewById(R.id.tv_login);
            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: ", e);
        }
    }
    
    private void initDatabase() {
        try {
            database = AppDatabase.getDatabase(this);
            userDao = database.userDao();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }
    
    private void setListeners() {
        try {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUser();
                }
            });
            
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error opening login activity: ", e);
                        Toast.makeText(RegisterActivity.this, "Error opening login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            // Add listener for role selection changes
            rgUserRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    try {
                        updateUIForRole();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onCheckedChanged: ", e);
                    }
                }
            });

            btnRegSetGps.setOnClickListener(v -> checkPermissionAndSetGps());
            btnRegSelectMap.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, MapPickerActivity.class);
                mapPickerLauncher.launch(intent);
            });
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
    }
    
    private void updateUIForRole() {
        try {
            if (rbDoctor.isChecked()) {
                etSpecialization.setVisibility(View.VISIBLE);
                etLocation.setVisibility(View.VISIBLE);
                layoutDoctorLocation.setVisibility(View.VISIBLE);
                
                // Also show parent layouts if they are TextInputLayouts
                View specParent = (View) etSpecialization.getParent().getParent();
                View locParent = (View) etLocation.getParent().getParent();
                
                if (specParent != null) specParent.setVisibility(View.VISIBLE);
                if (locParent != null) locParent.setVisibility(View.VISIBLE);
            } else {
                etSpecialization.setVisibility(View.GONE);
                etLocation.setVisibility(View.GONE);
                layoutDoctorLocation.setVisibility(View.GONE);
                
                View specParent = (View) etSpecialization.getParent().getParent();
                View locParent = (View) etLocation.getParent().getParent();
                
                if (specParent != null) specParent.setVisibility(View.GONE);
                if (locParent != null) locParent.setVisibility(View.GONE);
            }
            Log.d(TAG, "UI updated for role selection");
        } catch (Exception e) {
            Log.e(TAG, "Error in updateUIForRole: ", e);
        }
    }
    
    private void registerUser() {
        try {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = rbPatient.isChecked() ? "patient" : "doctor";
            String specialization = etSpecialization.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            
            Log.d(TAG, "Registering user with role: " + role);
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (role.equals("doctor") && (specialization.isEmpty() || location.isEmpty())) {
                Toast.makeText(this, "Please fill doctor-specific fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if user already exists
            UserEntity existingUser = userDao.getUserByEmail(email);
            if (existingUser != null) {
                Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "Creating user with email: " + email);
            
            // Create new user
            String userId = UUID.randomUUID().toString();
            UserEntity user = new UserEntity(userId, name, email, password, role);
            
            if (role.equals("patient")) {
                // Do nothing for patient specific for now
            } else {
                user.setSpecialization(specialization);
                user.setLocation(location);
                user.setLatitude(doctorLat);
                user.setLongitude(doctorLng);
                user.setRating(0.0);
                user.setFees(500); // Default fees
            }
            
            // Insert user into database
            new Thread(() -> {
                try {
                    userDao.insertUser(user);
                    runOnUiThread(() -> {
                        Log.d(TAG, "User registration successful");
                        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to Login Activity instead of dashboard
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        
                        // Pass email optionally so it pre-fills? Not needed, just start activity
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Log.w(TAG, "User registration failed");
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error in registerUser: ", e);
            Toast.makeText(this, "Error initiating registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initMapLauncher() {
        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        doctorLat = result.getData().getDoubleExtra("lat", 0);
                        doctorLng = result.getData().getDoubleExtra("lng", 0);
                        updateAddressFromCoords(doctorLat, doctorLng);
                        Toast.makeText(this, "Location set from map.", Toast.LENGTH_SHORT).show();
                        btnRegSelectMap.setText("Location Set");
                    }
                }
        );
    }

    private void checkPermissionAndSetGps() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        fetchGpsLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(RegisterActivity.this, "Location permission required", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void fetchGpsLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    doctorLat = location.getLatitude();
                    doctorLng = location.getLongitude();
                    updateAddressFromCoords(doctorLat, doctorLng);
                    Toast.makeText(this, "GPS location set.", Toast.LENGTH_SHORT).show();
                    btnRegSetGps.setText("GPS Set");
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception", e);
        }
    }

    private void updateAddressFromCoords(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                etLocation.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
        }
    }
}