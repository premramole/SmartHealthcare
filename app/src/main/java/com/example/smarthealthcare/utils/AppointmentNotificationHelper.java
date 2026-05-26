package com.example.smarthealthcare.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppointmentNotificationHelper {
    
    private static final String TAG = "AppointmentNotificationHelper";
    private static final String NOTIFICATIONS_COLLECTION = "notifications";
    
    /**
     * Send appointment confirmation notification to patient
     */
    public static void sendAppointmentConfirmation(String patientId, String doctorName, String appointmentTime) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", patientId);
        notification.put("title", "Appointment Confirmed");
        notification.put("body", "Your appointment with " + doctorName + " on " + appointmentTime + " has been confirmed.");
        notification.put("type", "appointment");
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        
        db.collection(NOTIFICATIONS_COLLECTION)
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Appointment confirmation notification sent successfully");
                    // TODO: Send FCM notification
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending appointment confirmation notification", e);
                });
    }
    
    /**
     * Send appointment reminder notification to patient
     */
    public static void sendAppointmentReminder(String patientId, String doctorName, String appointmentTime) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", patientId);
        notification.put("title", "Appointment Reminder");
        notification.put("body", "Reminder: Your appointment with " + doctorName + " is scheduled for " + appointmentTime + ".");
        notification.put("type", "reminder");
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        
        db.collection(NOTIFICATIONS_COLLECTION)
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Appointment reminder notification sent successfully");
                    // TODO: Send FCM notification
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending appointment reminder notification", e);
                });
    }
    
    /**
     * Send new message notification
     */
    public static void sendNewMessageNotification(String recipientId, String senderName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", recipientId);
        notification.put("title", "New Message");
        notification.put("body", "You have a new message from " + senderName + ".");
        notification.put("type", "message");
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        
        db.collection(NOTIFICATIONS_COLLECTION)
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "New message notification sent successfully");
                    // TODO: Send FCM notification
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending new message notification", e);
                });
    }
    
    /**
     * Send report update notification
     */
    public static void sendReportUpdateNotification(String patientId, String doctorName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", patientId);
        notification.put("title", "Report Update");
        notification.put("body", doctorName + " has updated your medical report.");
        notification.put("type", "report");
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        
        db.collection(NOTIFICATIONS_COLLECTION)
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Report update notification sent successfully");
                    // TODO: Send FCM notification
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending report update notification", e);
                });
    }
}