package com.example.smarthealthcare.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.DoctorHistoryAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.entities.AppointmentEntity;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoctorHistoryActivity extends AppCompatActivity {

    private static final String TAG = "DoctorHistory";
    private RecyclerView recyclerView;
    private DoctorHistoryAdapter adapter;
    private List<AppointmentEntity> allAppointments = new ArrayList<>();
    private List<AppointmentEntity> filteredAppointments = new ArrayList<>();
    private AppDatabase database;
    private String currentUserId;
    private String currentSearchQuery = "";
    private String currentStatusFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_history);

        initViews();
        database = AppDatabase.getDatabase(this);
        
        SharedPreferences prefs = getSharedPreferences("SmartHealthcarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", null);

        if (currentUserId != null) {
            loadHistory();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                applyFilters();
                return true;
            }
        });

        ChipGroup filterGroup = findViewById(R.id.chip_group_filters);
        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all) currentStatusFilter = "all";
            else if (checkedId == R.id.chip_completed) currentStatusFilter = "completed";
            else if (checkedId == R.id.chip_pending) currentStatusFilter = "pending";
            else if (checkedId == R.id.chip_cancelled) currentStatusFilter = "cancelled";
            applyFilters();
        });
    }

    private void loadHistory() {
        database.appointmentDao().getAppointmentsForDoctor(currentUserId).observe(this, appointments -> {
            if (appointments != null) {
                allAppointments.clear();
                allAppointments.addAll(appointments);
                // Sort by createdAt descending (newest first)
                Collections.sort(allAppointments, (a1, a2) -> Long.compare(a2.getCreatedAt(), a1.getCreatedAt()));
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        filteredAppointments.clear();
        for (AppointmentEntity appt : allAppointments) {
            boolean matchesSearch = TextUtils.isEmpty(currentSearchQuery) || 
                                   appt.getPatientName().toLowerCase().contains(currentSearchQuery.toLowerCase());
            
            boolean matchesStatus = currentStatusFilter.equals("all") || 
                                   appt.getStatus().equalsIgnoreCase(currentStatusFilter);

            if (matchesSearch && matchesStatus) {
                filteredAppointments.add(appt);
            }
        }

        if (adapter == null) {
            adapter = new DoctorHistoryAdapter(filteredAppointments, currentUserId);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
