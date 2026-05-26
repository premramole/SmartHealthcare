package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthealthcare.R;
import com.example.smarthealthcare.activities.AppointmentBookingActivity;
import com.example.smarthealthcare.database.entities.UserEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    
    private List<UserEntity> doctors;
    private android.location.Location userLocation;
    
    public DoctorAdapter(List<UserEntity> doctors) {
        this.doctors = doctors;
    }

    public void setUserLocation(android.location.Location location) {
        this.userLocation = location;
        notifyDataSetChanged();
    }
    
    public void updateList(List<UserEntity> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        UserEntity doctor = doctors.get(position);
        holder.bind(doctor, userLocation);
    }
    
    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }
    
    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvSpecialization;
        private TextView tvRating;
        private TextView tvLocation;
        private TextView tvFees;
        private TextView tvAvailability;
        private TextView tvDistance;
        private Button btnBook;
        private Context context;
        
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvName = itemView.findViewById(R.id.tv_name);
            tvSpecialization = itemView.findViewById(R.id.tv_specialization);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvFees = itemView.findViewById(R.id.tv_fees);
            tvAvailability = itemView.findViewById(R.id.tv_availability);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            btnBook = itemView.findViewById(R.id.btn_book);
        }
        
        public void bind(UserEntity doctor, android.location.Location userLocation) {
            tvName.setText(doctor.getName() != null ? doctor.getName() : "Unknown");
            tvSpecialization.setText(doctor.getSpecialization() != null ? doctor.getSpecialization() : "General");
            tvRating.setText(String.format(java.util.Locale.US, "%.1f", doctor.getRating()));
            tvLocation.setText(doctor.getLocation() != null ? doctor.getLocation() : "Clinic");
            tvFees.setText("₹" + doctor.getFees());
            
            if (userLocation != null && doctor.getLatitude() != 0) {
                float[] results = new float[1];
                android.location.Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                        doctor.getLatitude(), doctor.getLongitude(), results);
                float distanceInMeters = results[0];
                if (tvDistance != null) {
                    tvDistance.setVisibility(View.VISIBLE);
                    if (distanceInMeters < 1000) {
                        tvDistance.setText(String.format(java.util.Locale.US, "%.0f m away", distanceInMeters));
                    } else {
                        tvDistance.setText(String.format(java.util.Locale.US, "%.1f km away", distanceInMeters / 1000f));
                    }
                }
            } else if (tvDistance != null) {
                tvDistance.setVisibility(View.GONE);
            }
            
            // Calculate Availability
            String availableDays = doctor.getAvailableDays(); // e.g. "Mon,Tue,Wed"
            String timeSlots = doctor.getTimeSlots();       // e.g. "09:00 AM - 05:00 PM"
            
            String currentDay = new SimpleDateFormat("EEE", Locale.US).format(new Date());
            boolean isAvailableToday = false;
            if (availableDays != null && availableDays.toLowerCase().contains(currentDay.toLowerCase())) {
                isAvailableToday = true;
            }

            StringBuilder availabilityText = new StringBuilder();
            if (isAvailableToday) {
                availabilityText.append("● Available Today");
                tvAvailability.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                availabilityText.append("○ Not Available Today");
                tvAvailability.setTextColor(context.getResources().getColor(R.color.red));
            }

            tvAvailability.setText(availabilityText.toString());
            btnBook.setEnabled(true);
            btnBook.setAlpha(1.0f);
            
            btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AppointmentBookingActivity.class);
                    intent.putExtra("doctor_id", doctor.getUserId());
                    intent.putExtra("doctor_name", doctor.getName());
                    intent.putExtra("doctor_fees", doctor.getFees());
                    context.startActivity(intent);
                }
            });
        }
    }
}