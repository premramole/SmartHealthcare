package com.example.smarthealthcare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.Appointment;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    
    public DoctorAppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }
    
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    
    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPatientName, tvDateTime, tvMode, tvStatus;
        private Button btnAccept, btnReject;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvMode = itemView.findViewById(R.id.tv_mode);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
        
        public void bind(Appointment appointment) {
            // TODO: Fetch patient name from Firestore
            tvPatientName.setText("Patient Name");
            tvDateTime.setText(appointment.getDateTime());
            tvMode.setText(appointment.getMode());
            tvStatus.setText(appointment.getStatus());
            
            // Show/hide action buttons based on status
            if ("pending".equals(appointment.getStatus())) {
                btnAccept.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            } else {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }
        }
    }
}