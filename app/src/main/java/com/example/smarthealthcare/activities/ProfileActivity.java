package com.example.smarthealthcare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    
    private CircleImageView imgProfile;
    private TextInputEditText etName, etEmail, etPhone;
    private Button btnSave, btnLogout;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initViews();
        initFirebase();
        loadUserProfile();
        setListeners();
    }
    
    private void initViews() {
        imgProfile = findViewById(R.id.img_profile);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        btnLogout = findViewById(R.id.btn_logout);
    }
    
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    
    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        etEmail.setText(mAuth.getCurrentUser().getEmail());
        
        db.collection("users").document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        etName.setText(document.getString("name"));
                        etPhone.setText(document.getString("phone"));
                    }
                }
            });
    }
    
    private void setListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    
    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        if (name.isEmpty()) {
            return;
        }
        
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        
        db.collection("users").document(userId)
            .update(user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Profile updated successfully
                }
            });
    }
    
    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}