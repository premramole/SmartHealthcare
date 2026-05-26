package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.entities.MedicalReportEntity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    
    private List<MedicalReportEntity> reports;
    private Context context;
    private boolean readOnly;
    
    public ReportAdapter(List<MedicalReportEntity> reports) {
        this.reports = reports;
        this.readOnly = false;
    }
    
    public ReportAdapter(List<MedicalReportEntity> reports, boolean readOnly) {
        this.reports = reports;
        this.readOnly = readOnly;
    }
    
    public void updateList(List<MedicalReportEntity> newReports) {
        this.reports.clear();
        this.reports.addAll(newReports);
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        this.context = parent.getContext();
        return new ReportViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        MedicalReportEntity report = reports.get(position);
        holder.bind(report);
    }
    
    @Override
    public int getItemCount() {
        return reports != null ? reports.size() : 0;
    }
    
    class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName;
        private TextView tvUploadTime;
        private Button btnDownload;
        private Button btnDelete;
        
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvUploadTime = itemView.findViewById(R.id.tv_upload_time);
            btnDownload = itemView.findViewById(R.id.btn_download);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
        
        public void bind(MedicalReportEntity report) {
            String fileName = new File(report.getReportFilePath()).getName();
            // Remove the prepended timestamp if possible for cleaner UI
            if (fileName.contains("_")) {
                fileName = fileName.substring(fileName.indexOf('_') + 1);
            }
            tvFileName.setText(fileName);
            
            // Convert timestamp to readable format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            String formattedTime = sdf.format(new Date(report.getCreatedAt()));
            tvUploadTime.setText("Uploaded: " + formattedTime);
            
            btnDownload.setOnClickListener(v -> {
                File file = new File(report.getReportFilePath());
                if (!file.exists()) {
                    Toast.makeText(context, "File not found on this device", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    // Determine MIME type from file extension BEFORE getting the URI
                    // (ContentResolver.getType on a FileProvider URI returns null)
                    String name = file.getName().toLowerCase(Locale.getDefault());
                    String mimeType;
                    if (name.endsWith(".pdf")) {
                        mimeType = "application/pdf";
                    } else if (name.endsWith(".png")) {
                        mimeType = "image/png";
                    } else {
                        mimeType = "image/jpeg";
                    }

                    Uri fileUri = FileProvider.getUriForFile(
                            context,
                            "com.example.smarthealthcare.provider",
                            file
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, mimeType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(intent, "Open with"));
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            if (readOnly) {
                btnDelete.setVisibility(View.GONE);
            } else {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> {
                    btnDelete.setEnabled(false); // prevent double clicks
                    new Thread(() -> {
                        try {
                            // 1. Delete from Room DB
                            com.example.smarthealthcare.database.AppDatabase.getDatabase(context)
                                .medicalReportDao().deleteReport(report.getReportId());
                            
                            // 2. Delete physical file
                            File file = new File(report.getReportFilePath());
                            if (file.exists()) {
                                file.delete();
                            }
                            
                            // 3. Update UI
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    reports.remove(pos);
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                Toast.makeText(context, "Error deleting report", Toast.LENGTH_SHORT).show();
                                btnDelete.setEnabled(true);
                            });
                        }
                    }).start();
                });
            }
        }
    }
}