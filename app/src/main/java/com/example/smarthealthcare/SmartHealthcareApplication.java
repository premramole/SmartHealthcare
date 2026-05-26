package com.example.smarthealthcare;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.example.smarthealthcare.utils.NotificationUtils;

public class SmartHealthcareApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Firebase Cloud Messaging is disabled for the offline version
        // NotificationUtils.subscribeToTopic("appointments");
        // NotificationUtils.subscribeToTopic("messages");
        // NotificationUtils.subscribeToTopic("reports");
    }
}