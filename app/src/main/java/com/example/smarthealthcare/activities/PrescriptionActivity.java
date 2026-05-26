package com.example.smarthealthcare.activities;

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
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.dao.PrescriptionDao;
import com.example.smarthealthcare.database.entities.PrescriptionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PrescriptionActivity extends AppCompatActivity {
    
    private static final String TAG = "PrescriptionActivity";
    
    private TextView tvPatientName;
    private EditText etDiagnosis, etMedicines, etDosage, etInstructions;
    private Button btnCreatePrescription;
    
    private String appointmentId, patientId, patientName, doctorId;
    private AppDatabase database;
    private PrescriptionDao prescriptionDao;
    private AppointmentDao appointmentDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        
        initDatabase();
        getIntentData();
        initViews();
        setData();
        setListeners();
    }
    
    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        prescriptionDao = database.prescriptionDao();
        appointmentDao = database.appointmentDao();
    }
    
    private void getIntentData() {
        appointmentId = getIntent().getStringExtra("appointment_id");
        patientId = getIntent().getStringExtra("patient_id");
        patientName = getIntent().getStringExtra("patient_name");
        
        // Ensure doctorId is passed or obtained
        doctorId = getSharedPreferences("SmartHealthcarePrefs", MODE_PRIVATE).getString("user_id", "");
        
        // Fallback for DoctorDashboard button testing which only passes user_id
        if (appointmentId == null && getIntent().hasExtra("user_id")) {
             doctorId = getIntent().getStringExtra("user_id");
             Log.w(TAG, "Opened without appointment data. Usually for reviewing.");
        }
    }
    
    private void initViews() {
        tvPatientName = findViewById(R.id.tv_patient_name);
        etDiagnosis = findViewById(R.id.et_diagnosis);
        etMedicines = findViewById(R.id.et_medicines);
        etDosage = findViewById(R.id.et_dosage);
        etInstructions = findViewById(R.id.et_instructions);
        btnCreatePrescription = findViewById(R.id.btn_create_prescription);
    }
    
    private void setData() {
        if (patientName != null && !patientName.isEmpty()) {
            tvPatientName.setText("Patient: " + patientName);
        } else if (patientId != null) {
            tvPatientName.setText("Patient ID: " + patientId);
        } else {
            tvPatientName.setText("No Patient Data");
        }
    }
    
    private void setListeners() {
        btnCreatePrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPrescription();
            }
        });
    }
    
    private void createPrescription() {
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "No appointment selected to prescribe for.", Toast.LENGTH_SHORT).show();
            return;
        }

        String diagnosis = etDiagnosis.getText().toString().trim();
        String medicines = etMedicines.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        
        if (diagnosis.isEmpty() || medicines.isEmpty() || dosage.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String prescriptionId = "PRES_" + UUID.randomUUID().toString();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        PrescriptionEntity prescription = new PrescriptionEntity(
            prescriptionId,
            appointmentId,
            patientId,
            doctorId,
            diagnosis,
            medicines,
            dosage,
            instructions,
            currentDate
        );
        
        new Thread(() -> {
            try {
                // Save to Room
                prescriptionDao.insertPrescription(prescription);
                
                // Update appointment status to completed
                appointmentDao.updateAppointmentStatus(appointmentId, "completed");
                
                runOnUiThread(() -> {
                    Log.d(TAG, "Prescription created with ID: " + prescriptionId);
                    Toast.makeText(this, "Prescription created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error creating prescription: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Error creating prescription: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}