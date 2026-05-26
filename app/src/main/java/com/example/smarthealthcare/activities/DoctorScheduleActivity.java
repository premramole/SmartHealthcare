package com.example.smarthealthcare.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleActivity extends AppCompatActivity {

    private static final String TAG = "DoctorSchedule";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";

    private TextInputEditText etFees, etTimeSlots;
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private Button btnSave;

    private UserDao userDao;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule);

        userDao = AppDatabase.getDatabase(this).userDao();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        currentUserId = getIntent().getStringExtra("user_id");
        if (currentUserId == null) {
            currentUserId = prefs.getString(KEY_USER_ID, null);
        }

        initViews();
        loadCurrentSettings();

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void initViews() {
        etFees = findViewById(R.id.et_fees);
        etTimeSlots = findViewById(R.id.et_time_slots);
        cbMonday    = findViewById(R.id.cb_monday);
        cbTuesday   = findViewById(R.id.cb_tuesday);
        cbWednesday = findViewById(R.id.cb_wednesday);
        cbThursday  = findViewById(R.id.cb_thursday);
        cbFriday    = findViewById(R.id.cb_friday);
        cbSaturday  = findViewById(R.id.cb_saturday);
        cbSunday    = findViewById(R.id.cb_sunday);
        btnSave     = findViewById(R.id.btn_save_schedule);
    }

    private void loadCurrentSettings() {
        if (currentUserId == null) return;
        new Thread(() -> {
            try {
                UserEntity user = userDao.getUserById(currentUserId);
                if (user != null) {
                    runOnUiThread(() -> {
                        etFees.setText(String.valueOf(user.getFees()));
                        String slots = user.getTimeSlots();
                        if (slots != null) etTimeSlots.setText(slots);

                        String days = user.getAvailableDays();
                        if (days != null) {
                            cbMonday.setChecked(days.contains("Mon"));
                            cbTuesday.setChecked(days.contains("Tue"));
                            cbWednesday.setChecked(days.contains("Wed"));
                            cbThursday.setChecked(days.contains("Thu"));
                            cbFriday.setChecked(days.contains("Fri"));
                            cbSaturday.setChecked(days.contains("Sat"));
                            cbSunday.setChecked(days.contains("Sun"));
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading settings", e);
            }
        }).start();
    }

    private void saveSettings() {
        if (currentUserId == null) {
            Toast.makeText(this, "User not identified", Toast.LENGTH_SHORT).show();
            return;
        }

        String feesStr = etFees.getText() != null ? etFees.getText().toString().trim() : "";
        String slots   = etTimeSlots.getText() != null ? etTimeSlots.getText().toString().trim() : "";

        int fees = 0;
        if (!feesStr.isEmpty()) {
            try { fees = Integer.parseInt(feesStr); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid fees amount", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Build available days string
        List<String> selectedDays = new ArrayList<>();
        if (cbMonday.isChecked())    selectedDays.add("Mon");
        if (cbTuesday.isChecked())   selectedDays.add("Tue");
        if (cbWednesday.isChecked()) selectedDays.add("Wed");
        if (cbThursday.isChecked())  selectedDays.add("Thu");
        if (cbFriday.isChecked())    selectedDays.add("Fri");
        if (cbSaturday.isChecked())  selectedDays.add("Sat");
        if (cbSunday.isChecked())    selectedDays.add("Sun");
        String days = android.text.TextUtils.join(",", selectedDays);

        final int finalFees = fees;
        final String finalDays = days;
        final String finalSlots = slots;

        new Thread(() -> {
            try {
                userDao.updateDoctorSchedule(currentUserId, finalFees, finalDays, finalSlots);
                runOnUiThread(() -> {
                    Toast.makeText(this, "✅ Settings saved successfully!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error saving settings", e);
                runOnUiThread(() ->
                    Toast.makeText(this, "Error saving settings", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
