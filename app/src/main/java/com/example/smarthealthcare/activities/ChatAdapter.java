package com.example.smarthealthcare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;
    private String currentUserId;
    
    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
    
    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
    
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.bind(message);
    }
    
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    
    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage, tvTimestamp;
        
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
        
        public void bind(ChatMessage message) {
            tvMessage.setText(message.getMessageText());
            // TODO: Format timestamp
            tvTimestamp.setText(String.valueOf(message.getTimestamp()));
            
            // Set alignment based on sender
            if (currentUserId != null && currentUserId.equals(message.getSenderId())) {
                // Align to right for sent messages
                ((View) tvMessage.getParent()).setRotationY(180);
            } else {
                // Align to left for received messages
                ((View) tvMessage.getParent()).setRotationY(0);
            }
        }
    }
}