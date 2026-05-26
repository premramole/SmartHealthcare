package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.ReportAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.entities.MedicalReportEntity;

import java.util.ArrayList;

public class PatientReportsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private TextView tvNoReports;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reports);

        String patientId = getIntent().getStringExtra("patient_id");
        String patientName = getIntent().getStringExtra("patient_name");

        tvTitle = findViewById(R.id.tv_title);
        tvNoReports = findViewById(R.id.tv_no_reports);
        recyclerView = findViewById(R.id.recycler_view_reports);

        if (patientName != null && !patientName.isEmpty()) {
            tvTitle.setText(patientName + "'s Reports");
        }

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        if (patientId == null || patientId.isEmpty()) {
            tvNoReports.setText("Could not identify patient.");
            tvNoReports.setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<MedicalReportEntity> list = new ArrayList<>();
        // Use a simple ReportAdapter; hide the Delete button after binding via the adapter's constructor
        reportAdapter = new ReportAdapter(list, true); // "true" = read-only (doctor view)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reportAdapter);

        AppDatabase.getDatabase(this)
                .medicalReportDao()
                .getReportsForPatient(patientId)
                .observe(this, reports -> {
                    if (reports == null || reports.isEmpty()) {
                        tvNoReports.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoReports.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        reportAdapter.updateList(reports);
                    }
                });
    }
}
