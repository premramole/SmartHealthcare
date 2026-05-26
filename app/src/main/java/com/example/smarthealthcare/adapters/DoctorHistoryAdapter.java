package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.entities.AppointmentEntity;

import java.util.List;

public class DoctorHistoryAdapter extends RecyclerView.Adapter<DoctorHistoryAdapter.ViewHolder> {

    private List<AppointmentEntity> appointmentList;
    private Context context;
    private String doctorId;

    public DoctorHistoryAdapter(List<AppointmentEntity> appointmentList, String doctorId) {
        this.appointmentList = appointmentList;
        this.doctorId = doctorId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentEntity appointment = appointmentList.get(position);

        holder.tvPatientName.setText(appointment.getPatientName());
        holder.tvDate.setText(appointment.getAppointmentDate());
        
        String symptoms = appointment.getSymptoms();
        holder.tvSymptoms.setText((symptoms == null || symptoms.isEmpty()) ? "No symptoms provided" : symptoms);
        
        String status = appointment.getStatus();
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        // Color coding for status
        int colorRes;
        switch (status.toLowerCase()) {
            case "completed":
                colorRes = R.color.green;
                break;
            case "pending":
                colorRes = R.color.orange;
                break;
            case "cancelled":
                colorRes = R.color.red;
                break;
            default:
                colorRes = R.color.grey;
                break;
        }
        holder.tvStatus.setBackgroundTintList(context.getResources().getColorStateList(colorRes));

        holder.btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.smarthealthcare.activities.HealthHistoryActivity.class);
            intent.putExtra("patient_id", appointment.getPatientId());
            intent.putExtra("patient_name", appointment.getPatientName());
            intent.putExtra("doctor_id", doctorId); // Pass doctorId to restrict view
            context.startActivity(intent);
        });

        holder.btnViewReports.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.smarthealthcare.activities.PatientReportsActivity.class);
            intent.putExtra("patient_id", appointment.getPatientId());
            intent.putExtra("patient_name", appointment.getPatientName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvStatus, tvDate, tvSymptoms;
        Button btnViewHistory, btnViewReports;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_appointment_date);
            tvSymptoms = itemView.findViewById(R.id.tv_symptoms);
            btnViewHistory = itemView.findViewById(R.id.btn_view_patient_history);
            btnViewReports = itemView.findViewById(R.id.btn_view_patient_reports);
        }
    }
}
