package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.HistoryRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthHistoryAdapter extends RecyclerView.Adapter<HealthHistoryAdapter.ViewHolder> {

    private List<HistoryRecord> recordList;
    private Context context;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    public HealthHistoryAdapter(List<HistoryRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_health_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryRecord record = recordList.get(position);
        
        Date date = new Date(record.getTimestamp());
        holder.tvDate.setText(dayFormat.format(date));
        holder.tvYear.setText(yearFormat.format(date));
        
        holder.tvTitle.setText(record.getTitle());
        holder.tvDoctor.setText(record.getDoctorName());
        holder.tvSummary.setText(record.getSummary());
        
        switch (record.getType()) {
            case APPOINTMENT:
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_today);
                holder.tvType.setText("Appointment");
                holder.tvType.setBackgroundResource(R.drawable.bg_status_badge);
                holder.tvType.setBackgroundTintList(context.getResources().getColorStateList(R.color.purple_500));
                break;
            case PRESCRIPTION:
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_agenda);
                holder.tvType.setText("Prescription");
                holder.tvType.setBackgroundResource(R.drawable.bg_status_badge);
                holder.tvType.setBackgroundTintList(context.getResources().getColorStateList(R.color.teal_700));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvYear, tvTitle, tvDoctor, tvSummary, tvType;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_record_date);
            tvYear = itemView.findViewById(R.id.tv_record_year);
            tvTitle = itemView.findViewById(R.id.tv_record_title);
            tvDoctor = itemView.findViewById(R.id.tv_record_doctor);
            tvSummary = itemView.findViewById(R.id.tv_record_summary);
            tvType = itemView.findViewById(R.id.tv_record_type);
            ivIcon = itemView.findViewById(R.id.iv_record_icon);
        }
    }
}
