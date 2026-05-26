package com.example.smarthealthcare.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smarthealthcare.R;

public class VideoCallActivity extends AppCompatActivity {

    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private WebView webView;
    private ProgressBar progressBar;
    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        webView = findViewById(R.id.webview_video_call);
        progressBar = findViewById(R.id.progress_bar);

        appointmentId = getIntent().getStringExtra("appointment_id");

        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Valid Appointment ID required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check and request runtime permissions before setting up WebView
        if (checkPermissions()) {
            setupWebView();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && audioPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                CAMERA_MIC_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                setupWebView();
            } else {
                Toast.makeText(this, "Camera and Microphone permissions are required for Video Calls", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // Required explicitly for WebRTC (Jitsi) in WebView
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                // Automatically grant permissions requested by the web page (Camera, Mic)
                // This will only succeed if Android OS-level permissions were granted above
                runOnUiThread(() -> request.grant(request.getResources()));
            }
        });

        // Load the Jitsi Meet web interface directly
        String roomUrl = "https://meet.jit.si/SmartHealth_" + appointmentId;
        webView.loadUrl(roomUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Explicitly destroy webview to release camera/mic immediately upon exiting
            if (webView != null) {
                webView.loadUrl("about:blank");
                webView.destroy();
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        super.onDestroy();
    }
}