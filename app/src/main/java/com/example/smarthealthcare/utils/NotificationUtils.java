package com.example.smarthealthcare.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationUtils {
    
    private static final String TAG = "NotificationUtils";
    
    public static void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to " + topic + " topic";
                    if (!task.isSuccessful()) {
                        msg = "Failed to subscribe to " + topic + " topic";
                    }
                    Log.d(TAG, msg);
                });
    }
    
    public static void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Unsubscribed from " + topic + " topic";
                    if (!task.isSuccessful()) {
                        msg = "Failed to unsubscribe from " + topic + " topic";
                    }
                    Log.d(TAG, msg);
                });
    }
    
    // TODO: Implement method to send notifications to specific users
    // This would typically be done from your server-side code
}