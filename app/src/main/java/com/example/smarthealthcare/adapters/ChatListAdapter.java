package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.activities.ChatActivity;
import com.example.smarthealthcare.models.ChatPartner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<ChatPartner> chatPartners;
    private String currentUserId;
    private Context context;

    public ChatListAdapter(List<ChatPartner> chatPartners, String currentUserId) {
        this.chatPartners = chatPartners;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        ChatPartner partner = chatPartners.get(position);
        holder.bind(partner);
    }

    @Override
    public int getItemCount() {
        return chatPartners.size();
    }

    public void updateList(List<ChatPartner> newList) {
        this.chatPartners = newList;
        notifyDataSetChanged();
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvLastMsg, tvTime;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_patient_name);
            tvLastMsg = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_last_message_time);
        }

        public void bind(ChatPartner partner) {
            tvName.setText(partner.getUserName());
            tvLastMsg.setText(partner.getLastMessage());
            
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(partner.getLastMessageTime())));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_id", currentUserId);
                intent.putExtra("target_user_id", partner.getUserId());
                intent.putExtra("target_user_name", partner.getUserName());
                context.startActivity(intent);
            });
        }
    }
}
