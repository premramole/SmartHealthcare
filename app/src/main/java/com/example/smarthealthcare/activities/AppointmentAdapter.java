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

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    
    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
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
        private TextView tvDoctorName, tvDateTime, tvMode, tvStatus;
        private Button btnAction;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tv_doctor_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvMode = itemView.findViewById(R.id.tv_mode);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
        
        public void bind(Appointment appointment) {
            // TODO: Fetch doctor name from Firestore
            tvDoctorName.setText("Doctor Name");
            tvDateTime.setText(appointment.getDateTime());
            tvMode.setText(appointment.getMode());
            tvStatus.setText(appointment.getStatus());
            
            // Set button text based on status
            switch (appointment.getStatus()) {
                case "pending":
                    btnAction.setText("Cancel");
                    break;
                case "confirmed":
                    btnAction.setText("Reschedule");
                    break;
                case "completed":
                    btnAction.setText("View Prescription");
                    break;
                default:
                    btnAction.setVisibility(View.GONE);
            }
        }
    }
}