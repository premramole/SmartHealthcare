package com.example.smarthealthcare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.UserEntity;

/**
 * Utility class to prevent common crashes and handle errors gracefully
 */
public class CrashPrevention {
    
    private static final String TAG = "CrashPrevention";
    private static final String PREFS_NAME = "SmartHealthcarePrefs";
    private static final String KEY_USER_ID = "user_id";
    
    /**
     * Safely get user from database with null check
     */
    public static UserEntity getUserSafely(UserDao userDao, String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                Log.w(TAG, "getUserSafely: userId is null or empty");
                return null;
            }
            return userDao.getUserById(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error in getUserSafely: ", e);
            return null;
        }
    }
    
    /**
     * Safely get string from intent
     */
    public static String getStringSafely(Intent intent, String key) {
        try {
            if (intent == null) {
                Log.w(TAG, "getStringSafely: intent is null");
                return "";
            }
            String value = intent.getStringExtra(key);
            return value != null ? value : "";
        } catch (Exception e) {
            Log.e(TAG, "Error in getStringSafely: ", e);
            return "";
        }
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String userId = prefs.getString(KEY_USER_ID, null);
            return userId != null && !userId.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status: ", e);
            return false;
        }
    }
    
    /**
     * Get current user ID safely
     */
    public static String getCurrentUserId(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getString(KEY_USER_ID, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user ID: ", e);
            return null;
        }
    }
    
    /**
     * Navigate to dashboard with error handling
     */
    public static void navigateToDashboard(AppCompatActivity activity, UserEntity user) {
        try {
            if (user == null) {
                Log.w(TAG, "navigateToDashboard: user is null");
                Toast.makeText(activity, "Error: User data not found", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String role = user.getRole();
            if (role == null || role.isEmpty()) {
                Log.w(TAG, "navigateToDashboard: role is null or empty, defaulting to patient");
                role = "patient";
            }
            
            Log.d(TAG, "Navigating to dashboard for role: " + role);
            Intent intent;
            if ("doctor".equalsIgnoreCase(role.trim())) {
                intent = new Intent(activity, com.example.smarthealthcare.activities.DoctorDashboardActivity.class);
            } else {
                intent = new Intent(activity, com.example.smarthealthcare.activities.PatientDashboardActivity.class);
            }
            
            // Pass user data safely
            intent.putExtra("user_id", user.getUserId());
            intent.putExtra("user_name", user.getName());
            intent.putExtra("user_email", user.getEmail());
            intent.putExtra("user_role", user.getRole());
            
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in navigateToDashboard: ", e);
            Toast.makeText(activity, "Error during navigation", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Show error message with logging
     */
    public static void showError(Activity activity, String message) {
        try {
            Log.e(TAG, "Error: " + message);
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showError: ", e);
        }
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }
}
