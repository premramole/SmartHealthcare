package com.example.smarthealthcare.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smarthealthcare.database.entities.ChatMessageEntity;

import java.util.List;

@Dao
public interface ChatMessageDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMessage(ChatMessageEntity message);
    
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    LiveData<List<ChatMessageEntity>> getChatMessages(String userId1, String userId2);
    
    @Query("SELECT * FROM chat_messages WHERE senderId = :senderId ORDER BY timestamp DESC")
    LiveData<List<ChatMessageEntity>> getSentMessages(String senderId);
    
    @Query("SELECT * FROM chat_messages WHERE receiverId = :receiverId ORDER BY timestamp DESC")
    LiveData<List<ChatMessageEntity>> getReceivedMessages(String receiverId);
    
    @Query("DELETE FROM chat_messages WHERE messageId = :messageId")
    int deleteMessage(String messageId);
    
    @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    LiveData<List<ChatMessageEntity>> getMessagesForUser(String userId);

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    LiveData<List<ChatMessageEntity>> getAllMessages();
}
