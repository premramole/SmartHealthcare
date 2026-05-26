package com.example.smarthealthcare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> reports;
    
    public ReportAdapter(List<Report> reports) {
        this.reports = reports;
    }
    
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.bind(report);
    }
    
    @Override
    public int getItemCount() {
        return reports.size();
    }
    
    class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName, tvUploadTime;
        private Button btnDownload, btnDelete;
        
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvUploadTime = itemView.findViewById(R.id.tv_upload_time);
            btnDownload = itemView.findViewById(R.id.btn_download);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
        
        public void bind(Report report) {
            tvFileName.setText(report.getFileName());
            // TODO: Format timestamp
            tvUploadTime.setText(String.valueOf(report.getUploadTimestamp()));
        }
    }
}