package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class BackendTestActivity extends AppCompatActivity {
    private static final String TAG = "BackendTestActivity";
    
    private TextView textViewResult;
    private Button buttonTestConnection;
    private Button buttonCreateUser;
    
    private ApiInterface apiInterface;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_test);
        
        initViews();
        initApi();
        setupClickListeners();
    }
    
    private void initViews() {
        textViewResult = findViewById(R.id.text_view_result);
        buttonTestConnection = findViewById(R.id.button_test_connection);
        buttonCreateUser = findViewById(R.id.button_create_user);
    }
    
    private void initApi() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }
    
    private void setupClickListeners() {
        buttonTestConnection.setOnClickListener(v -> testConnection());
        buttonCreateUser.setOnClickListener(v -> createUser());
    }
    
    private void testConnection() {
        textViewResult.setText("Testing connection...");
        
        Call<java.util.List<User>> call = apiInterface.getAllUsers();
        call.enqueue(new Callback<java.util.List<User>>() {
            @Override
            public void onResponse(Call<java.util.List<User>> call, Response<java.util.List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    textViewResult.setText("Connection successful! Found " + response.body().size() + " users.");
                } else {
                    textViewResult.setText("Connection failed with code: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<java.util.List<User>> call, Throwable t) {
                Log.e(TAG, "Network request failed", t);
                textViewResult.setText("Connection failed: " + t.getMessage());
                Toast.makeText(BackendTestActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void createUser() {
        textViewResult.setText("Creating user...");
        
        User user = new User();
        user.setUserId("android_user_" + System.currentTimeMillis());
        user.setName("Android User");
        user.setEmail("android" + System.currentTimeMillis() + "@example.com");
        user.setRole("patient");
        
        Call<User> call = apiInterface.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User createdUser = response.body();
                    textViewResult.setText("User created successfully!\nID: " + createdUser.getUserId() + 
                                         "\nName: " + createdUser.getName());
                } else {
                    textViewResult.setText("Failed to create user. Code: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Network request failed", t);
                textViewResult.setText("Failed to create user: " + t.getMessage());
                Toast.makeText(BackendTestActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}