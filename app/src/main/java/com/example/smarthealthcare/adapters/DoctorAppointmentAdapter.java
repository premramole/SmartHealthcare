package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.activities.PrescriptionActivity;
import com.example.smarthealthcare.activities.PatientReportsActivity;
import com.example.smarthealthcare.activities.VideoCallActivity;
import com.example.smarthealthcare.models.Appointment;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.DoctorAppointmentViewHolder> {
    
    private List<Appointment> appointments;
    private Context context;
    private AppointmentDao appointmentDao;
    
    public DoctorAppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    @NonNull
    @Override
    public DoctorAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_appointment, parent, false);
        this.context = parent.getContext();
        // Initialize DAO here since we need Context
        this.appointmentDao = AppDatabase.getDatabase(context).appointmentDao();
        return new DoctorAppointmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorAppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }
    
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    
    class DoctorAppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPatientName;
        private TextView tvDateTime;
        private TextView tvMode;
        private TextView tvStatus;
        private Button btnAccept;
        private Button btnReject;
        private ImageButton btnVideoCall;
        private ImageButton btnChat;
        private Button btnPrescription;
        private Button btnViewReports;
        private Button btnComplete;
        
        public DoctorAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvMode = itemView.findViewById(R.id.tv_mode);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnVideoCall = itemView.findViewById(R.id.btn_video_call);
            btnChat = itemView.findViewById(R.id.btn_chat);
            btnPrescription = itemView.findViewById(R.id.btn_prescription);
            btnViewReports = itemView.findViewById(R.id.btn_view_reports);
            btnComplete = itemView.findViewById(R.id.btn_complete);
        }
        
        public void bind(Appointment appointment) {
            // Use patient name if available, otherwise use patient ID
            if (appointment.getPatientName() != null && !appointment.getPatientName().isEmpty()) {
                tvPatientName.setText(appointment.getPatientName());
            } else {
                tvPatientName.setText("Patient ID: " + appointment.getPatientId());
            }
            
            tvDateTime.setText(appointment.getDateTime());
            tvMode.setText(appointment.getMode());
            
            // Set status text and color
            tvStatus.setText(appointment.getStatus());
            if ("pending".equalsIgnoreCase(appointment.getStatus())) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
            } else if ("confirmed".equalsIgnoreCase(appointment.getStatus())) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else if ("rejected".equalsIgnoreCase(appointment.getStatus())) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            } else if ("completed".equalsIgnoreCase(appointment.getStatus())) {
                tvStatus.setTextColor(context.getResources().getColor(R.color.grey));
            }
            
            // Show/hide action buttons based on status
            if ("pending".equalsIgnoreCase(appointment.getStatus())) {
                btnAccept.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
                btnVideoCall.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnPrescription.setVisibility(View.GONE);
                btnViewReports.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
            } else if ("confirmed".equalsIgnoreCase(appointment.getStatus())) {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                
                boolean isOnline = "Online".equalsIgnoreCase(appointment.getMode());
                btnVideoCall.setVisibility(isOnline ? View.VISIBLE : View.GONE);
                btnChat.setVisibility(isOnline ? View.VISIBLE : View.GONE);
                
                btnPrescription.setVisibility(View.VISIBLE);
                btnViewReports.setVisibility(View.VISIBLE);
                btnComplete.setVisibility(View.VISIBLE);
            } else {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                btnVideoCall.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnPrescription.setVisibility(View.GONE);
                btnViewReports.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
            }
            
            // Set click listeners for action buttons
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAppointmentStatus(appointment.getAppointmentId(), "confirmed");
                }
            });
            
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAppointmentStatus(appointment.getAppointmentId(), "rejected");
                }
            });
            
            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAppointmentStatus(appointment.getAppointmentId(), "completed");
                }
            });
            
            // Video call button
            btnVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoCallActivity.class);
                    intent.putExtra("appointment_id", appointment.getAppointmentId());
                    intent.putExtra("patient_id", appointment.getPatientId());
                    context.startActivity(intent);
                }
            });

            // Chat button
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, com.example.smarthealthcare.activities.ChatActivity.class);
                    intent.putExtra("user_id", appointment.getDoctorId());
                    intent.putExtra("target_user_id", appointment.getPatientId());
                    intent.putExtra("target_user_name", appointment.getPatientName() != null ? appointment.getPatientName() : "Patient");
                    context.startActivity(intent);
                }
            });
            
            // View Reports button
            btnViewReports.setOnClickListener(v -> {
                Intent intent = new Intent(context, PatientReportsActivity.class);
                intent.putExtra("patient_id", appointment.getPatientId());
                intent.putExtra("patient_name", appointment.getPatientName() != null ? appointment.getPatientName() : "Patient");
                context.startActivity(intent);
            });

            // Prescription button
            btnPrescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start prescription activity
                    Intent intent = new Intent(context, PrescriptionActivity.class);
                    intent.putExtra("appointment_id", appointment.getAppointmentId());
                    intent.putExtra("patient_id", appointment.getPatientId());
                    intent.putExtra("patient_name", appointment.getPatientName());
                    context.startActivity(intent);
                }
            });
        }
        
        private void updateAppointmentStatus(String appointmentId, String status) {
            new Thread(() -> {
                try {
                    appointmentDao.updateAppointmentStatus(appointmentId, status);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            // Update the appointment in the list in memory (optional, Room LiveData will auto-refresh it)
                            // We shouldn't strictly need this if we're using LiveData to refresh the whole list,
                            // but earlier code did it. So let's keep it just in case LiveData drops.
                            Toast.makeText(context, "Appointment " + status, Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        }
    }
}