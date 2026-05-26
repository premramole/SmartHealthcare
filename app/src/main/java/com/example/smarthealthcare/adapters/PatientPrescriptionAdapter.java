package com.example.smarthealthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.entities.PrescriptionEntity;

import java.util.List;

public class PatientPrescriptionAdapter extends RecyclerView.Adapter<PatientPrescriptionAdapter.PrescriptionViewHolder> {
    
    private List<PrescriptionEntity> prescriptions;
    
    public PatientPrescriptionAdapter(List<PrescriptionEntity> prescriptions) {
        this.prescriptions = prescriptions;
    }
    
    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        PrescriptionEntity prescription = prescriptions.get(position);
        holder.bind(prescription);
    }
    
    @Override
    public int getItemCount() {
        return prescriptions.size();
    }
    
    class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvDiagnosis;
        private TextView tvMedicines;
        private TextView tvDosage;
        private TextView tvInstructions;
        
        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDiagnosis = itemView.findViewById(R.id.tv_diagnosis);
            tvMedicines = itemView.findViewById(R.id.tv_medicines);
            tvDosage = itemView.findViewById(R.id.tv_dosage);
            tvInstructions = itemView.findViewById(R.id.tv_instructions);
        }
        
        public void bind(PrescriptionEntity prescription) {
            tvDate.setText("Date: " + prescription.getDate());
            tvDiagnosis.setText("Diagnosis: " + prescription.getDiagnosis());
            tvMedicines.setText("Medicines: " + prescription.getMedicines());
            tvDosage.setText("Dosage: " + prescription.getDosage());
            tvInstructions.setText("Instructions: " + prescription.getInstructions());
        }
    }
}
