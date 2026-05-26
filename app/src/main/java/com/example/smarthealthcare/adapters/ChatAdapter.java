package com.example.smarthealthcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.database.entities.ChatMessageEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    
    private List<ChatMessageEntity> chatMessages;
    private Context context;
    private String currentUserId;
    
    public ChatAdapter(List<ChatMessageEntity> chatMessages, String currentUserId) {
        this.chatMessages = chatMessages;
        this.currentUserId = currentUserId;
    }
    
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view;
        if (viewType == 0) {
            // Sent message
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_sent, parent, false);
        } else {
            // Received message
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_received, parent, false);
        }
        return new ChatViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessageEntity chatMessage = chatMessages.get(position);
        holder.bind(chatMessage);
    }
    
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        ChatMessageEntity chatMessage = chatMessages.get(position);
        if (currentUserId != null && currentUserId.equals(chatMessage.getSenderId())) {
            return 0; // Sent message
        } else {
            return 1; // Received message
        }
    }
    
    public void updateMessages(List<ChatMessageEntity> newMessages) {
        this.chatMessages.clear();
        this.chatMessages.addAll(newMessages);
        notifyDataSetChanged();
    }
    
    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTimestamp;
        
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
        
        public void bind(ChatMessageEntity chatMessage) {
            tvMessage.setText(chatMessage.getMessageText());
            try {
                // Convert timestamp to readable format
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(new Date(Long.parseLong(chatMessage.getTimestamp())));
                tvTimestamp.setText(formattedTime);
            } catch (Exception e) {
                tvTimestamp.setText("");
            }
        }
    }
}