package com.example.smarthealthcare.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.PatientPrescriptionAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.PrescriptionDao;
import com.example.smarthealthcare.database.entities.PrescriptionEntity;

import java.util.ArrayList;
import java.util.List;

public class PatientPrescriptionsActivity extends AppCompatActivity {
    
    private static final String TAG = "PatientPrescriptions";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";

    private RecyclerView recyclerView;
    private PatientPrescriptionAdapter prescriptionAdapter;
    private List<PrescriptionEntity> prescriptions;
    private TextView tvNoPrescriptions;
    
    private AppDatabase database;
    private PrescriptionDao prescriptionDao;
    private SharedPreferences sharedPreferences;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_prescriptions);

        initDatabase();
        initViews();
        loadPrescriptions();
    }

    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        prescriptionDao = database.prescriptionDao();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getString(KEY_USER_ID, null);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_prescriptions);
        tvNoPrescriptions = findViewById(R.id.tv_no_prescriptions);
        
        prescriptions = new ArrayList<>();
        prescriptionAdapter = new PatientPrescriptionAdapter(prescriptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(prescriptionAdapter);
    }

    private void loadPrescriptions() {
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        prescriptionDao.getPatientPrescriptions(currentUserId).observe(this, new Observer<List<PrescriptionEntity>>() {
            @Override
            public void onChanged(List<PrescriptionEntity> prescriptionEntities) {
                prescriptions.clear();
                if (prescriptionEntities != null && !prescriptionEntities.isEmpty()) {
                    prescriptions.addAll(prescriptionEntities);
                    tvNoPrescriptions.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    tvNoPrescriptions.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                prescriptionAdapter.notifyDataSetChanged();
            }
        });
    }
}
