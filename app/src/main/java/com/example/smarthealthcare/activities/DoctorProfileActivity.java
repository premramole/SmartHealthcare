package com.example.smarthealthcare.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileActivity extends AppCompatActivity {
    
    private static final String TAG = "DoctorProfile";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";
    
    private CircleImageView imgProfile;
    private TextInputEditText etName, etEmail, etPhone, etSpecialization, etLocation, etFees, etAvailableDays, etTimeSlots;
    private Button btnSave, btnLogout, btnSetGps, btnSelectMap;
    
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<Intent> mapPickerLauncher;
    
    private AppDatabase database;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private UserEntity currentUser;
    private String currentUserId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        
        try {
            setContentView(R.layout.activity_doctor_profile);
            Log.d(TAG, "Layout inflated successfully");
            
            initMapLauncher();
            initViews();
            initDatabase();
            loadDoctorProfile();
            setListeners();
            
            Log.d(TAG, "onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing profile screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        try {
            imgProfile = findViewById(R.id.img_profile);
            etName = findViewById(R.id.et_name);
            etEmail = findViewById(R.id.et_email);
            etPhone = findViewById(R.id.et_phone);
            etSpecialization = findViewById(R.id.et_specialization);
            etLocation = findViewById(R.id.et_location);
            etFees = findViewById(R.id.et_fees);
            etAvailableDays = findViewById(R.id.et_available_days);
            etTimeSlots = findViewById(R.id.et_time_slots);
            btnSave = findViewById(R.id.btn_save);
            btnLogout = findViewById(R.id.btn_logout);
            btnSetGps = findViewById(R.id.btn_set_gps);
            btnSelectMap = findViewById(R.id.btn_select_map);
            
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
            currentUserId = sharedPreferences.getString(KEY_USER_ID, null);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Database: ", e);
        }
    }
    
    private void loadDoctorProfile() {
        try {
            Log.d(TAG, "Loading doctor profile");
            
            if (currentUserId == null) {
                Log.w(TAG, "User not authenticated");
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            Log.d(TAG, "Loading profile for user ID: " + currentUserId);
            currentUser = userDao.getUserById(currentUserId);
            
            if (currentUser != null) {
                etEmail.setText(currentUser.getEmail());
                etName.setText(currentUser.getName());
                etSpecialization.setText(currentUser.getSpecialization());
                etLocation.setText(currentUser.getLocation());
                etFees.setText(String.valueOf(currentUser.getFees()));
                etAvailableDays.setText(currentUser.getAvailableDays());
                etTimeSlots.setText(currentUser.getTimeSlots());
            } else {
                Log.w(TAG, "User document not found");
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadDoctorProfile: ", e);
            Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setListeners() {
        try {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProfile();
                }
            });
            
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            btnSetGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissionAndSetGps();
                }
            });

            btnSelectMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DoctorProfileActivity.this, MapPickerActivity.class);
                    mapPickerLauncher.launch(intent);
                }
            });
            
            Log.d(TAG, "Listeners set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners: ", e);
        }
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
                        Toast.makeText(DoctorProfileActivity.this, "Location permission is required to set coordinates", Toast.LENGTH_SHORT).show();
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
                    if (currentUser != null) {
                        currentUser.setLatitude(location.getLatitude());
                        currentUser.setLongitude(location.getLongitude());
                        updateAddressFromCoords(location.getLatitude(), location.getLongitude());
                        Toast.makeText(this, "GPS coordinates updated. Click 'Save Changes' to persist.", Toast.LENGTH_LONG).show();
                        btnSetGps.setText("GPS Set");
                        btnSetGps.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                } else {
                    Toast.makeText(this, "Could not fetch current location. Make sure GPS is on.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception fetching location", e);
        }
    }

    private void initMapLauncher() {
        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        double lat = result.getData().getDoubleExtra("lat", 0);
                        double lng = result.getData().getDoubleExtra("lng", 0);
                        if (currentUser != null) {
                            currentUser.setLatitude(lat);
                            currentUser.setLongitude(lng);
                            updateAddressFromCoords(lat, lng);
                            Toast.makeText(this, "Map location selected. Click 'Save Changes' to persist.", Toast.LENGTH_LONG).show();
                            btnSelectMap.setText("Location Set");
                        }
                    }
                }
        );
    }

    private void updateAddressFromCoords(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                etLocation.setText(addressText);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching address", e);
        }
    }

    private void saveProfile() {
        try {
            String name = etName.getText().toString().trim();
            String specialization = etSpecialization.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String feesStr = etFees.getText().toString().trim();
            
            Log.d(TAG, "Saving profile data");
            
            if (name.isEmpty() || specialization.isEmpty() || location.isEmpty() || feesStr.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String availableDays = etAvailableDays.getText().toString().trim();
            String timeSlots = etTimeSlots.getText().toString().trim();
            
            int fees;
            try {
                fees = Integer.parseInt(feesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid fees", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (currentUser == null) {
                Log.w(TAG, "User not loaded");
                Toast.makeText(this, "User data not loaded", Toast.LENGTH_SHORT).show();
                return;
            }
            
            currentUser.setName(name);
            currentUser.setSpecialization(specialization);
            currentUser.setLocation(location);
            currentUser.setFees(fees);
            currentUser.setAvailableDays(availableDays);
            currentUser.setTimeSlots(timeSlots);
            
            Log.d(TAG, "Updating user document: " + currentUserId);
            
            new Thread(() -> {
                try {
                    userDao.updateUser(currentUser);
                    runOnUiThread(() -> {
                        Log.d(TAG, "Profile updated successfully");
                        Toast.makeText(DoctorProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error in saveProfile update: ", e);
                    runOnUiThread(() -> Toast.makeText(DoctorProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error in saveProfile: ", e);
            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void logout() {
        try {
            Log.d(TAG, "Logging out");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(DoctorProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: ", e);
            Toast.makeText(this, "Error during logout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}