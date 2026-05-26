package com.example.smarthealthcare.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.AppointmentDao;
import com.example.smarthealthcare.database.entities.AppointmentEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AppointmentBookingActivity extends AppCompatActivity {
    
    private static final String TAG = "AppointmentBooking";
    private TextView tvDoctorName, tvFees, tvSchedule;
    private EditText etDate, etTime;
    private RadioGroup rgMode;
    private RadioButton rbOnline, rbOffline;
    private Button btnBook;
    
    private String doctorId, doctorName, patientName;
    private int doctorFees;
    private String currentUserId;
    
    private AppDatabase database;
    private AppointmentDao appointmentDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        
        initDatabase();
        initViews();
        getIntentData();
        setData();
        setListeners();
    }
    
    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        appointmentDao = database.appointmentDao();
    }
    
    private void initViews() {
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvFees = findViewById(R.id.tv_fees);
        tvSchedule = findViewById(R.id.tv_schedule);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        rgMode = findViewById(R.id.rg_mode);
        rbOnline = findViewById(R.id.rb_online);
        rbOffline = findViewById(R.id.rb_offline);
        btnBook = findViewById(R.id.btn_book);
        
        // Prevent manual typing for date and time fields
        etDate.setFocusable(false);
        etDate.setClickable(true);
        etTime.setFocusable(false);
        etTime.setClickable(true);
    }
    
    private void getIntentData() {
        doctorId = getIntent().getStringExtra("doctor_id");
        doctorName = getIntent().getStringExtra("doctor_name");
        doctorFees = getIntent().getIntExtra("doctor_fees", 0);
        
        SharedPreferences prefs = getSharedPreferences("SmartHealthcarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", "");
    }
    
    private void setData() {
        tvDoctorName.setText(doctorName != null ? doctorName : "Doctor");
        tvFees.setText("Fees: ₹" + doctorFees);
        
        // Fetch full doctor schedule and current patient name
        if (doctorId != null && !doctorId.isEmpty()) {
            new Thread(() -> {
                com.example.smarthealthcare.database.entities.UserEntity doctor = database.userDao().getUserById(doctorId);
                com.example.smarthealthcare.database.entities.UserEntity patient = database.userDao().getUserById(currentUserId);
                
                runOnUiThread(() -> {
                    if (doctor != null) {
                        String days = doctor.getAvailableDays() != null && !doctor.getAvailableDays().isEmpty() ? doctor.getAvailableDays() : "Not specified";
                        String slots = doctor.getTimeSlots() != null && !doctor.getTimeSlots().isEmpty() ? doctor.getTimeSlots() : "Not specified";
                        
                        // Structure the display for a more professional look
                        String formattedSchedule = "📅 Days: " + days + "\n⏰ Time: " + slots;
                        tvSchedule.setText(formattedSchedule);
                    }
                    if (patient != null) {
                        patientName = patient.getName();
                    }
                });
            }).start();
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(new Date()));
        etTime.setText("10:00 AM");
    }
    
    private void setListeners() {
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                AppointmentBookingActivity.this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    Calendar finalCal = Calendar.getInstance();
                    finalCal.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDate.setText(sdf.format(finalCal.getTime()));
                },
                year, month, day
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        
        etTime.setOnClickListener(v -> {
            Calendar timeCalendar = Calendar.getInstance();
            int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = timeCalendar.get(Calendar.MINUTE);
            
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                AppointmentBookingActivity.this,
                (timeView, selectedHour, selectedMinute) -> {
                    Calendar finalCal = Calendar.getInstance();
                    finalCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                    finalCal.set(Calendar.MINUTE, selectedMinute);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    etTime.setText(sdf.format(finalCal.getTime()));
                },
                hour, minute, false
            );
            timePickerDialog.show();
        });

        btnBook.setOnClickListener(v -> bookAppointment());
    }
    
    private void bookAppointment() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String mode = rbOnline.isChecked() ? "online" : "offline";
        
        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String appointmentId = "APT_" + UUID.randomUUID().toString();
        String dateTime = date + " " + time;
        
        AppointmentEntity appointment = new AppointmentEntity(
                appointmentId, 
                doctorId, 
                currentUserId, 
                (patientName != null) ? patientName : "Patient", 
                dateTime, 
                mode, 
                "pending"
        );
        
        new Thread(() -> {
            try {
                appointmentDao.insertAppointment(appointment);
                runOnUiThread(() -> {
                    Toast.makeText(AppointmentBookingActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error booking: ", e);
                runOnUiThread(() -> Toast.makeText(AppointmentBookingActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}