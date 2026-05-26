package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.User;
import com.example.smarthealthcare.network.ApiClient;
import com.example.smarthealthcare.network.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterWithBackendActivity extends AppCompatActivity {
    
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgUserRole;
    private RadioButton rbPatient, rbDoctor;
    private Button btnRegister;
    private TextView tvLogin;
    
    private ApiInterface apiInterface;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initApi();
        setListeners();
    }
    
    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgUserRole = findViewById(R.id.rg_user_role);
        rbPatient = findViewById(R.id.rb_patient);
        rbDoctor = findViewById(R.id.rb_doctor);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
    }
    
    private void initApi() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }
    
    private void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterWithBackendActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = rbPatient.isChecked() ? "patient" : "doctor";
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create user object
        User user = new User();
        user.setUserId("user_" + System.currentTimeMillis()); // Generate a unique user ID
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        
        // Set default values based on role
        if (role.equals("patient")) {
            user.setMedicalHistory("");
        } else {
            user.setSpecialization("");
            user.setRating(0.0);
            user.setLocation("");
            user.setFees(0);
        }
        
        // Register user via backend API
        Call<User> call = apiInterface.registerUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterWithBackendActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Navigate to appropriate dashboard
                    Intent intent = role.equals("patient") ?
                            new Intent(RegisterWithBackendActivity.this, PatientDashboardActivity.class) :
                            new Intent(RegisterWithBackendActivity.this, DoctorDashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(RegisterWithBackendActivity.this, "User with this email or ID already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterWithBackendActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterWithBackendActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}