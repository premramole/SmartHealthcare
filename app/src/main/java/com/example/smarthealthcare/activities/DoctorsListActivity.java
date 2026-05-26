package com.example.smarthealthcare.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.DoctorAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;

import java.util.List;

public class DoctorsListActivity extends AppCompatActivity {
    
    private static final String TAG = "DoctorsListActivity";
    
    private RecyclerView recyclerView;
    private DoctorAdapter doctorAdapter;
    private TextView tvNoDoctors;
    private AppDatabase database;
    private UserDao userDao;
    private String patientId;
    
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private boolean isNearMeMode = false;
    private List<UserEntity> allDoctorsList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);
        
        initDatabase();
        initViews();
        loadDoctors();
    }
    
    private void initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    
    private void initDatabase() {
        try {
            database = AppDatabase.getDatabase(this);
            userDao = database.userDao();
            Log.d(TAG, "Database initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: ", e);
        }
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_doctors);
        tvNoDoctors = findViewById(R.id.tv_no_doctors);
        
        // Initialize adapter with empty list
        doctorAdapter = new DoctorAdapter(new java.util.ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(doctorAdapter);
        
        // Initially hide RecyclerView and show loading message
        recyclerView.setVisibility(View.GONE);
        tvNoDoctors.setVisibility(View.VISIBLE);
        tvNoDoctors.setText("Loading doctors...");

        initLocationClient();
        
        ChipGroup chipGroup = findViewById(R.id.chip_group_filter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_near_me) {
                isNearMeMode = true;
                checkPermissionAndGetLocation();
            } else {
                isNearMeMode = false;
                applyFilters();
            }
        });
    }

    private void checkPermissionAndGetLocation() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(DoctorsListActivity.this, "Location permission required for Near Me mode", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.chip_all).performClick();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void getCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                userLocation = location;
                if (location == null) {
                    Toast.makeText(this, "Could not determine location", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.chip_all).performClick();
                } else {
                    applyFilters();
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception", e);
        }
    }
    
    private void loadDoctors() {
        try {
            Log.d(TAG, "Loading doctors from Room database");
            
            // Get patient ID from intent
            patientId = getIntent().getStringExtra("user_id");
            
            // Load all doctors from Room database
            userDao.getAllDoctors().observe(this, new Observer<List<UserEntity>>() {
                @Override
                public void onChanged(List<UserEntity> doctors) {
                    if (doctors != null) {
                        allDoctorsList.clear();
                        allDoctorsList.addAll(doctors);
                        applyFilters();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loadDoctors: ", e);
            showError("An error occurred. Please try again.");
        }
    }

    private void applyFilters() {
        List<UserEntity> filteredList = new ArrayList<>();
        
        if (isNearMeMode && userLocation != null) {
            List<NearbyDoctor> nearbyList = new ArrayList<>();
            for (UserEntity doctor : allDoctorsList) {
                if (doctor.getLatitude() == 0) continue;
                
                float[] results = new float[1];
                Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                        doctor.getLatitude(), doctor.getLongitude(), results);
                float distance = results[0];
                
                // Show doctors within 20km for "Near Me"
                if (distance <= 20000) {
                    nearbyList.add(new NearbyDoctor(doctor, distance));
                }
            }
            
            // Sort by distance
            Collections.sort(nearbyList, (d1, d2) -> Float.compare(d1.distance, d2.distance));
            
            for (NearbyDoctor nd : nearbyList) {
                filteredList.add(nd.doctor);
            }
            doctorAdapter.setUserLocation(userLocation);
        } else {
            filteredList.addAll(allDoctorsList);
            doctorAdapter.setUserLocation(null);
        }

        if (!filteredList.isEmpty()) {
            doctorAdapter.updateList(filteredList);
            tvNoDoctors.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            tvNoDoctors.setVisibility(View.VISIBLE);
            tvNoDoctors.setText(isNearMeMode ? "No doctors found nearby (within 20km)" : "No doctors available");
            recyclerView.setVisibility(View.GONE);
        }
    }

    private static class NearbyDoctor {
        UserEntity doctor;
        float distance;
        NearbyDoctor(UserEntity doctor, float distance) {
            this.doctor = doctor;
            this.distance = distance;
        }
    }
    
    private void showError(String message) {
        runOnUiThread(() -> {
            tvNoDoctors.setVisibility(View.VISIBLE);
            tvNoDoctors.setText(message);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}