package com.example.smarthealthcare.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.ReportAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.MedicalReportDao;
import com.example.smarthealthcare.database.entities.MedicalReportEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportUploadActivity extends AppCompatActivity {
    
    private static final String TAG = "ReportUploadActivity";
    private static final int PICK_FILE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<MedicalReportEntity> reports;
    
    private Button btnSelectFile;
    private Button btnUpload;
    private TextView tvSelectedFile;
    
    private Uri fileUri;
    private String fileName;
    
    private AppDatabase database;
    private MedicalReportDao reportDao;
    private String currentUserId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_upload);
        
        initDatabase();
        initViews();
        initReports();
        setListeners();
    }
    
    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        reportDao = database.medicalReportDao();
        
        SharedPreferences prefs = getSharedPreferences("SmartHealthcarePrefs", MODE_PRIVATE);
        currentUserId = prefs.getString("user_id", "");
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_reports);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnUpload = findViewById(R.id.btn_upload);
        tvSelectedFile = findViewById(R.id.tv_selected_file);
        btnUpload.setEnabled(false);
    }
    
    private void initReports() {
        reports = new ArrayList<>();
        reportAdapter = new ReportAdapter(reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reportAdapter);
        
        loadReportsFromLocalDb();
    }
    
    private void setListeners() {
        btnSelectFile.setOnClickListener(v -> checkPermissionAndSelectFile());
        
        btnUpload.setOnClickListener(v -> {
            if (fileUri != null) {
                copyFileAndSaveToDb();
            } else {
                Toast.makeText(ReportUploadActivity.this, "Please select a file first", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkPermissionAndSelectFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            selectFile();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                        PERMISSION_REQUEST_CODE);
            } else {
                selectFile();
            }
        }
    }
    
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "image/jpeg", "image/jpg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF or Image File"), PICK_FILE_REQUEST);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                Toast.makeText(this, "Permission denied. Cannot select file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                fileName = getFileNameFromUri(fileUri);
                if (isFileValid(fileUri)) {
                    tvSelectedFile.setText(fileName);
                    btnUpload.setEnabled(true);
                } else {
                    Toast.makeText(this, "Please select a PDF or image file (JPG, JPEG, PNG)", Toast.LENGTH_LONG).show();
                    tvSelectedFile.setText("No file selected");
                    fileUri = null;
                    btnUpload.setEnabled(false);
                }
            }
        }
    }
    
    private boolean isFileValid(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) {
            String extension = getFileExtension(uri);
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        
        if (mimeType != null) {
            return mimeType.equals("application/pdf") || mimeType.startsWith("image/");
        }
        
        String fileName = getFileNameFromUri(uri);
        return fileName != null && (fileName.toLowerCase().endsWith(".pdf") || 
                                   fileName.toLowerCase().endsWith(".jpg") || 
                                   fileName.toLowerCase().endsWith(".jpeg") || 
                                   fileName.toLowerCase().endsWith(".png"));
    }
    
    private String getFileExtension(Uri uri) {
        String filePath = uri.getPath();
        if (filePath != null) {
            int dotIndex = filePath.lastIndexOf('.');
            if (dotIndex != -1) {
                return filePath.substring(dotIndex + 1);
            }
        }
        return null;
    }
    
    private String getFileNameFromUri(Uri uri) {
        if (uri == null) return "Unknown file";
        String name = uri.getLastPathSegment();
        return name != null ? name : "Unknown file";
    }
    
    private void copyFileAndSaveToDb() {
        if (fileUri == null || currentUserId == null || currentUserId.isEmpty()) return;
        
        final Uri currentFileUri = fileUri;
        final String currentFileName = fileName;
        fileUri = null; // Clear it instantly to prevent double-clicks capturing it again!
        
        btnUpload.setEnabled(false);
        tvSelectedFile.setText("Saving report...");
        
        new Thread(() -> {
            try {
                // Copy file locally to app internal storage
                File destDir = new File(getFilesDir(), "medical_reports");
                if (!destDir.exists()) destDir.mkdirs();
                
                // Safely determine MIME type and secure the extension
                String mimeType = getContentResolver().getType(currentFileUri);
                String ext = "";
                if (mimeType != null) {
                    if (mimeType.equals("application/pdf")) ext = ".pdf";
                    else if (mimeType.equals("image/png")) ext = ".png";
                    else if (mimeType.startsWith("image/")) ext = ".jpg";
                }
                
                String secureFileName = currentFileName;
                if (!ext.isEmpty() && !secureFileName.toLowerCase().endsWith(ext)) {
                    secureFileName += ext;
                }
                
                String uniqueFileName = System.currentTimeMillis() + "_" + secureFileName;
                File destFile = new File(destDir, uniqueFileName);
                
                InputStream is = getContentResolver().openInputStream(currentFileUri);
                OutputStream os = new FileOutputStream(destFile);
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
                
                // Save to Room
                String reportId = UUID.randomUUID().toString();
                MedicalReportEntity report = new MedicalReportEntity(
                        reportId,
                        currentUserId,
                        destFile.getAbsolutePath(),
                        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date())
                );
                
                reportDao.insertReport(report);
                
                runOnUiThread(() -> {
                    Toast.makeText(ReportUploadActivity.this, "Report saved successfully", Toast.LENGTH_SHORT).show();
                    tvSelectedFile.setText("No file selected");
                    fileUri = null;
                    btnUpload.setEnabled(false);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Failed saving report", e);
                runOnUiThread(() -> {
                    Toast.makeText(ReportUploadActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnUpload.setEnabled(true);
                });
            }
        }).start();
    }
    
    private void loadReportsFromLocalDb() {
        if (currentUserId == null || currentUserId.isEmpty()) return;
        reportDao.getReportsForPatient(currentUserId).observe(this, reportEntities -> {
            if (reportEntities != null) {
                reportAdapter.updateList(reportEntities);
            }
        });
    }
}