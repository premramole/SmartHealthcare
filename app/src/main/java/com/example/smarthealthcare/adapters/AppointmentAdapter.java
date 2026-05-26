package com.example.smarthealthcare.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private List<AppointmentEntity> appointments;
    private String userRole;
    private AppointmentDao appointmentDao;
    private UserDao userDao;

    public AppointmentAdapter(Context context, List<AppointmentEntity> appointments, String userRole) {
        this.context = context;
        this.appointments = appointments;
        this.userRole = userRole;
        this.appointmentDao = AppDatabase.getDatabase(context).appointmentDao();
        this.userDao = AppDatabase.getDatabase(context).userDao();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentEntity appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDoctorName;
        private TextView tvDateTime;
        private TextView tvMode;
        private TextView tvStatus;
        private Button btnAction;
        private Button btnRateDoctor;
        private android.widget.ImageButton btnChat;
        private android.widget.ImageButton btnVideo;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName  = itemView.findViewById(R.id.tv_doctor_name);
            tvDateTime    = itemView.findViewById(R.id.tv_date_time);
            tvMode        = itemView.findViewById(R.id.tv_mode);
            tvStatus      = itemView.findViewById(R.id.tv_status);
            btnAction     = itemView.findViewById(R.id.btn_action);
            btnRateDoctor = itemView.findViewById(R.id.btn_rate_doctor);
            btnChat       = itemView.findViewById(R.id.btn_chat);
            btnVideo      = itemView.findViewById(R.id.btn_video);
        }

        public void bind(AppointmentEntity appointment) {
            if ("doctor".equals(userRole)) {
                tvDoctorName.setText("Patient: " + appointment.getPatientName());
            } else {
                com.example.smarthealthcare.database.entities.UserEntity doctor = userDao.getUserById(appointment.getDoctorId());
                if (doctor != null && doctor.getName() != null) {
                    tvDoctorName.setText(doctor.getName());
                } else {
                    tvDoctorName.setText("Doctor ID: " + appointment.getDoctorId());
                }
            }

            tvDateTime.setText(appointment.getAppointmentDate());
            tvMode.setText(appointment.getMode());

            // Set status text and color
            String status = appointment.getStatus();
            if (status != null) {
                tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
                if ("pending".equalsIgnoreCase(status)) {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
                } else if ("confirmed".equalsIgnoreCase(status)) {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                } else if ("rejected".equalsIgnoreCase(status)) {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                } else if ("completed".equalsIgnoreCase(status)) {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.grey));
                } else {
                    tvStatus.setTextColor(context.getResources().getColor(R.color.black));
                }
            } else {
                tvStatus.setText("Unknown");
            }

            // --- Rate Doctor button: only for patient + completed + not yet rated ---
            boolean showRateButton = "patient".equals(userRole)
                    && "completed".equalsIgnoreCase(status)
                    && appointment.getIsRated() == 0;
            btnRateDoctor.setVisibility(showRateButton ? View.VISIBLE : View.GONE);
            btnRateDoctor.setOnClickListener(v -> showRatingDialog(appointment));

            // --- Chat & Video: only for confirmed + online mode ---
            boolean isConfirmed = "confirmed".equalsIgnoreCase(status);
            boolean isOnline = "Online".equalsIgnoreCase(appointment.getMode());
            int visibility = (isConfirmed && isOnline) ? View.VISIBLE : View.GONE;

            if (btnChat != null) {
                btnChat.setVisibility(visibility);
                btnChat.setOnClickListener(v -> {
                    String targetId = "patient".equals(userRole) ? appointment.getDoctorId() : appointment.getPatientId();
                    String targetName = appointment.getPatientName();
                    if ("patient".equals(userRole)) {
                        com.example.smarthealthcare.database.entities.UserEntity doc = userDao.getUserById(appointment.getDoctorId());
                        targetName = (doc != null && doc.getName() != null) ? doc.getName() : "Doctor";
                    }
                    android.content.Intent chatIntent = new android.content.Intent(context, com.example.smarthealthcare.activities.ChatActivity.class);
                    chatIntent.putExtra("user_id", "patient".equals(userRole) ? appointment.getPatientId() : appointment.getDoctorId());
                    chatIntent.putExtra("target_user_id", targetId);
                    chatIntent.putExtra("target_user_name", targetName);
                    context.startActivity(chatIntent);
                });
            }

            if (btnVideo != null) {
                btnVideo.setVisibility(visibility);
                btnVideo.setOnClickListener(v -> {
                    android.content.Intent videoIntent = new android.content.Intent(context, com.example.smarthealthcare.activities.VideoCallActivity.class);
                    videoIntent.putExtra("appointment_id", appointment.getAppointmentId());
                    context.startActivity(videoIntent);
                });
            }

            // Set action button visibility/text
            if ("pending".equalsIgnoreCase(status)) {
                if ("doctor".equals(userRole)) {
                    btnAction.setText("Confirm");
                    btnAction.setVisibility(View.VISIBLE);
                    btnAction.setOnClickListener(v -> {
                        if (context instanceof com.example.smarthealthcare.activities.AppointmentActivity) {
                            ((com.example.smarthealthcare.activities.AppointmentActivity) context).confirmAppointment(appointment.getAppointmentId());
                        }
                    });
                } else {
                    btnAction.setText("Cancel");
                    btnAction.setVisibility(View.VISIBLE);
                    btnAction.setOnClickListener(v -> {
                        if (context instanceof com.example.smarthealthcare.activities.AppointmentActivity) {
                            ((com.example.smarthealthcare.activities.AppointmentActivity) context).cancelAppointment(appointment.getAppointmentId());
                        }
                    });
                }
            } else if ("confirmed".equalsIgnoreCase(status)) {
                if ("patient".equals(userRole)) {
                    btnAction.setText("Reschedule");
                    btnAction.setVisibility(View.VISIBLE);
                } else {
                    btnAction.setText("Complete");
                    btnAction.setVisibility(View.VISIBLE);
                    btnAction.setOnClickListener(v -> {
                        if (context instanceof com.example.smarthealthcare.activities.AppointmentActivity) {
                            ((com.example.smarthealthcare.activities.AppointmentActivity) context).completeAppointment(appointment.getAppointmentId());
                        }
                    });
                }
            } else {
                btnAction.setVisibility(View.GONE);
            }
        }

        private void showRatingDialog(AppointmentEntity appointment) {
            // Create a LinearLayout to hold the RatingBar and ensure it's centered
            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            int padding = (int) (16 * context.getResources().getDisplayMetrics().density);
            layout.setPadding(padding, padding, padding, padding);

            RatingBar ratingBar = new RatingBar(context);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(1f);
            ratingBar.setRating(5f);
            
            // Set layout params to wrap_content to prevent stretching
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            ratingBar.setLayoutParams(lp);
            layout.addView(ratingBar);

            new AlertDialog.Builder(context)
                    .setTitle("Rate Doctor")
                    .setMessage("How would you rate your consultation?")
                    .setView(layout)
                    .setPositiveButton("Submit", (dialog, which) -> {
                        float rating = ratingBar.getRating();
                        if (rating == 0) {
                            Toast.makeText(context, "Please select at least 1 star", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        submitRating(appointment, rating);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void submitRating(AppointmentEntity appointment, float rating) {
            new Thread(() -> {
                try {
                    // Save rating to the appointment record
                    appointmentDao.rateAppointment(appointment.getAppointmentId(), rating);

                    // Recalculate doctor's average rating and update UserEntity
                    double avgRating = userDao.getAverageDoctorRating(appointment.getDoctorId());
                    userDao.updateDoctorRating(appointment.getDoctorId(), avgRating);

                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context,
                                    "Thanks! Your " + (int) rating + "★ rating has been submitted.",
                                    Toast.LENGTH_SHORT).show();
                            btnRateDoctor.setVisibility(View.GONE);
                        });
                    }
                } catch (Exception e) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Error submitting rating", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }).start();
        }
    }
}