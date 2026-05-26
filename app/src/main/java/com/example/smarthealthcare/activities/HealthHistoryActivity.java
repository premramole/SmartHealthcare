package com.example.smarthealthcare.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.HealthHistoryAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.entities.AppointmentEntity;
import com.example.smarthealthcare.database.entities.PrescriptionEntity;
import com.example.smarthealthcare.database.entities.UserEntity;
import com.example.smarthealthcare.models.HistoryRecord;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthHistoryActivity extends AppCompatActivity {

    private static final String TAG = "HealthHistoryActivity";
    private RecyclerView recyclerView;
    private HealthHistoryAdapter adapter;
    private List<HistoryRecord> allRecords = new ArrayList<>();
    private List<HistoryRecord> filteredRecords = new ArrayList<>();
    private AppDatabase database;
    private String currentUserId, filterDoctorId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        initViews();
        database = AppDatabase.getDatabase(this);
        
        // Allow passing a patient ID and doctor ID from Intent (for restricted Doctor View)
        String intentPatientId = getIntent().getStringExtra("patient_id");
        String intentPatientName = getIntent().getStringExtra("patient_name");
        filterDoctorId = getIntent().getStringExtra("doctor_id");
        
        if (intentPatientId != null) {
            currentUserId = intentPatientId;
            if (getSupportActionBar() != null && intentPatientName != null) {
                String title = (filterDoctorId != null) ? "History with " + intentPatientName : intentPatientName + "'s History";
                getSupportActionBar().setTitle(title);
            }
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("SmartHealthcarePrefs", MODE_PRIVATE);
            currentUserId = sharedPreferences.getString("user_id", null);
        }

        if (currentUserId != null) {
            loadHistoryData();
        } else {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view_health_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChipGroup filterGroup = findViewById(R.id.chip_group_filters);
        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all) filterHistory(null);
            else if (checkedId == R.id.chip_appointments) filterHistory(HistoryRecord.Type.APPOINTMENT);
            else if (checkedId == R.id.chip_prescriptions) filterHistory(HistoryRecord.Type.PRESCRIPTION);
        });
    }

    private void loadHistoryData() {
        // Observe multiple LiveDatas and combine them
        database.appointmentDao().getAppointmentsForPatient(currentUserId).observe(this, appointments -> {
            database.prescriptionDao().getPatientPrescriptions(currentUserId).observe(this, prescriptions -> {
                combineAndDisplay(appointments, prescriptions);
            });
        });
    }

    private void combineAndDisplay(List<AppointmentEntity> appointments, List<PrescriptionEntity> prescriptions) {
        allRecords.clear();

        // Process Appointments
        if (appointments != null) {
            for (AppointmentEntity appt : appointments) {
                // If filterDoctorId is set, only show appointments with that doctor
                if (filterDoctorId != null && !appt.getDoctorId().equals(filterDoctorId)) continue;

                long timestamp = parseDate(appt.getAppointmentDate());
                UserEntity doctor = database.userDao().getUserById(appt.getDoctorId());
                String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
                
                allRecords.add(new HistoryRecord(
                        appt.getAppointmentId(),
                        HistoryRecord.Type.APPOINTMENT,
                        timestamp,
                        appt.getAppointmentDate(),
                        "Appointment",
                        doctorName,
                        "Status: " + appt.getStatus() + "\nMode: " + appt.getMode()
                ));
            }
        }

        // Process Prescriptions
        if (prescriptions != null) {
            for (PrescriptionEntity pres : prescriptions) {
                // If filterDoctorId is set, only show prescriptions from that doctor
                if (filterDoctorId != null && !pres.getDoctorId().equals(filterDoctorId)) continue;

                UserEntity doctor = database.userDao().getUserById(pres.getDoctorId());
                String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
                
                allRecords.add(new HistoryRecord(
                        pres.getPrescriptionId(),
                        HistoryRecord.Type.PRESCRIPTION,
                        pres.getTimestamp(),
                        pres.getDate(),
                        "Prescription",
                        doctorName,
                        "Diagnosis: " + pres.getDiagnosis() + "\nMedicines: " + pres.getMedicines()
                ));
            }
        }

        // Sort all records by timestamp descending
        Collections.sort(allRecords, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
        
        filterHistory(null); // Show all initially
    }

    private void filterHistory(HistoryRecord.Type type) {
        filteredRecords.clear();
        if (type == null) {
            filteredRecords.addAll(allRecords);
        } else {
            for (HistoryRecord record : allRecords) {
                if (record.getType() == type) {
                    filteredRecords.add(record);
                }
            }
        }
        
        if (adapter == null) {
            adapter = new HealthHistoryAdapter(filteredRecords);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private long parseDate(String dateStr) {
        try {
            if (dateStr == null) return System.currentTimeMillis();
            if (dateStr.contains(":")) {
                return dateTimeFormat.parse(dateStr).getTime();
            } else {
                return dateFormat.parse(dateStr).getTime();
            }
        } catch (ParseException e) {
            return System.currentTimeMillis();
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
