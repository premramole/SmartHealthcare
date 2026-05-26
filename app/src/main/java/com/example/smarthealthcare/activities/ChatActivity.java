package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.ChatAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.ChatMessageDao;
import com.example.smarthealthcare.database.entities.ChatMessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    
    private static final String TAG = "ChatActivity";
    
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessageEntity> chatMessages;
    private EditText etMessage;
    private Button btnSend;
    
    private String currentUserId;
    private String targetUserId;
    private String targetUserName;
    
    private AppDatabase database;
    private ChatMessageDao chatDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        getIntentData();
        initViews();
        initDatabase();
        setListeners();
    }
    
    private void getIntentData() {
        currentUserId = getIntent().getStringExtra("user_id");
        targetUserId = getIntent().getStringExtra("target_user_id");
        targetUserName = getIntent().getStringExtra("target_user_name");
        
        if (currentUserId == null || targetUserId == null) {
            Toast.makeText(this, "Error initializing chat. Missing user data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Optionally update the action bar title or a header TextView if you have one
        if (getSupportActionBar() != null && targetUserName != null) {
            getSupportActionBar().setTitle("Chat with " + targetUserName);
        }
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, currentUserId);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        // Scroll to bottom when keyboard opens
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (chatMessages.size() > 0) {
                                recyclerView.scrollToPosition(chatMessages.size() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });
    }
    
    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        chatDao = database.chatMessageDao();
        
        // Observe messages between current user and target user globally
        chatDao.getChatMessages(currentUserId, targetUserId).observe(this, messages -> {
            if (messages != null) {
                chatAdapter.updateMessages(messages);
                if (messages.size() > 0) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });
    }
    
    private void setListeners() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    
    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            
            String messageId = UUID.randomUUID().toString();
            String timestamp = String.valueOf(System.currentTimeMillis());
            
            ChatMessageEntity newMessage = new ChatMessageEntity(
                messageId,
                currentUserId,
                targetUserId,
                messageText,
                timestamp
            );
            
            // Insert to local room database
            new Thread(() -> {
                chatDao.insertMessage(newMessage);
                runOnUiThread(() -> {
                    // Clear input field on success
                    etMessage.setText("");
                });
            }).start();
        }
    }
}