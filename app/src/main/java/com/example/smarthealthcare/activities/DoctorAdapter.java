package com.example.smarthealthcare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private List<Doctor> doctors;
    
    public DoctorAdapter(List<Doctor> doctors) {
        this.doctors = doctors;
    }
    
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.bind(doctor);
    }
    
    @Override
    public int getItemCount() {
        return doctors.size();
    }
    
    class DoctorViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSpecialization, tvRating, tvLocation, tvFees;
        private Button btnBook;
        
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSpecialization = itemView.findViewById(R.id.tv_specialization);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvFees = itemView.findViewById(R.id.tv_fees);
            btnBook = itemView.findViewById(R.id.btn_book);
        }
        
        public void bind(Doctor doctor) {
            tvName.setText(doctor.getName());
            tvSpecialization.setText(doctor.getSpecialization());
            tvRating.setText(String.valueOf(doctor.getRating()));
            tvLocation.setText(doctor.getLocation());
            tvFees.setText("₹" + doctor.getFees());
        }
    }
}