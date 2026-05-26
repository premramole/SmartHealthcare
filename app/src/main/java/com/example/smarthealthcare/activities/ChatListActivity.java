package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.ChatListAdapter;
import com.example.smarthealthcare.database.AppDatabase;
import com.example.smarthealthcare.database.dao.ChatMessageDao;
import com.example.smarthealthcare.database.dao.UserDao;
import com.example.smarthealthcare.database.entities.ChatMessageEntity;
import com.example.smarthealthcare.database.entities.UserEntity;
import com.example.smarthealthcare.models.ChatPartner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private String currentUserId;
    private AppDatabase database;
    private ChatMessageDao chatDao;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        currentUserId = getIntent().getStringExtra("user_id");
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not identified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initDatabase();
        initViews();
        loadChatPartners();
    }

    private void initDatabase() {
        database = AppDatabase.getDatabase(this);
        chatDao = database.chatMessageDao();
        userDao = database.userDao();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(new ArrayList<>(), currentUserId);
        recyclerView.setAdapter(adapter);
    }

    private void loadChatPartners() {
        chatDao.getMessagesForUser(currentUserId).observe(this, messages -> {
            if (messages != null) {
                processMessages(messages);
            }
        });
    }

    private void processMessages(List<ChatMessageEntity> messages) {
        new Thread(() -> {
            Map<String, ChatPartner> partnerMap = new HashMap<>();
            
            for (ChatMessageEntity msg : messages) {
                String partnerId = msg.getSenderId().equals(currentUserId) ? msg.getReceiverId() : msg.getSenderId();
                
                if (!partnerMap.containsKey(partnerId)) {
                    UserEntity user = userDao.getUserById(partnerId);
                    String name = (user != null) ? user.getName() : "Unknown User";
                    
                    long time = 0;
                    try { time = Long.parseLong(msg.getTimestamp()); } catch (Exception e) {}
                    
                    partnerMap.put(partnerId, new ChatPartner(partnerId, name, msg.getMessageText(), time));
                }
            }
            
            List<ChatPartner> partnerList = new ArrayList<>(partnerMap.values());
            // Sort by time descending (latest first)
            partnerList.sort((p1, p2) -> Long.compare(p2.getLastMessageTime(), p1.getLastMessageTime()));
            
            runOnUiThread(() -> adapter.updateList(partnerList));
        }).start();
    }
}
