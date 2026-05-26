package com.example.smarthealthcare.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealthcare.R;
import com.example.smarthealthcare.adapters.ChatAdapter;
import com.example.smarthealthcare.database.entities.ChatMessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {

    private static final String TAG = "ChatbotActivity";

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessageEntity> messageList;
    private EditText etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        initViews();
        setupRecyclerView();
        setListeners();

        // Send welcome message
        addMessage("Hello! I'm your AI Health Assistant. Describe your symptoms and I'll help you understand what might be causing them. For example, you can tell me about headaches, fever, cough, or any other symptoms you're experiencing.", false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, "user");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Scroll to bottom when keyboard opens
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() -> {
                    if (messageList.size() > 0) {
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }, 100);
            }
        });
    }

    private void setListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        
        // Also send message when user presses enter in the EditText
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Add user message to UI
            addMessage(message, true);
            etMessage.setText("");

            // Process the message and generate a smart response
            generateSmartResponse(message);
        }
    }

    private void generateSmartResponse(String userMessage) {
        String botResponse = analyzeSymptoms(userMessage.toLowerCase(Locale.getDefault()));
        addMessage(botResponse, false);
    }

    // State machine for conversation context
    private enum ChatState {
        IDLE,
        ASKING_DURATION,
        ASKING_SEVERITY,
        ASKING_CONTEXT,
        FOLLOW_UP_COMPLETE
    }

    private ChatState currentState = ChatState.IDLE;
    private List<String> detectedSymptoms = new ArrayList<>();
    private String duration = "";
    private String severity = "";

    private String analyzeSymptoms(String message) {
        // Handle basic conversation and reset if needed
        if (message.equals("hi") || message.equals("hello") || message.equals("hey") || message.contains("good morning")) {
            resetConversation();
            return "Hello! I am your AI Health Assistant. Please describe what symptoms you are experiencing (e.g., fever, headache, bone pain).";
        }
        if (message.contains("thank") || message.contains("thanks")) {
            return "You're very welcome! Let me know if you need help with anything else. Take care!";
        }
        if (message.contains("reset") || message.contains("start over")) {
            resetConversation();
            return "Alright, let's start over. What symptoms are you experiencing?";
        }

        switch (currentState) {
            case IDLE:
                detectNewSymptoms(message);
                if (detectedSymptoms.isEmpty()) {
                    return "I couldn't identify specific symptoms. Could you describe what you're feeling? (e.g., 'I have a fever' or 'My head hurts').";
                }
                currentState = ChatState.ASKING_DURATION;
                return "I understand. Since when have you been experiencing these symptoms?";

            case ASKING_DURATION:
                duration = message;
                currentState = ChatState.ASKING_SEVERITY;
                return "And how would you describe the severity? (Mild, Moderate, or Severe?)";

            case ASKING_SEVERITY:
                severity = message;
                currentState = ChatState.FOLLOW_UP_COMPLETE;
                return generateFinalGuidance();

            case FOLLOW_UP_COMPLETE:
                // If they mention new symptoms, reset and start over
                if (hasSymptoms(message)) {
                    resetConversation();
                    detectNewSymptoms(message);
                    currentState = ChatState.ASKING_DURATION;
                    return "Re-evaluating with new symptoms. Since when are you feeling this?";
                }
                return "If you have more questions or new symptoms, feel free to share. Otherwise, I recommend following the guidance above and consulting a doctor.";

            default:
                return "I'm here to help. Could you tell me more about how you're feeling?";
        }
    }

    private void resetConversation() {
        currentState = ChatState.IDLE;
        detectedSymptoms.clear();
        duration = "";
        severity = "";
    }

    private void detectNewSymptoms(String message) {
        if (message.contains("headache") || message.contains("head pain") || message.contains("migraine") || message.contains("head hurts")) detectedSymptoms.add("headache");
        if (message.contains("fever") || message.contains("temperature") || message.contains("hot") || message.contains("chills")) detectedSymptoms.add("fever");
        if (message.contains("cough") || message.contains("cold") || message.contains("sneeze") || message.contains("runny nose")) detectedSymptoms.add("cough/cold");
        if (message.contains("stomach") || message.contains("belly") || message.contains("abdomen")) detectedSymptoms.add("stomach ache");
        if (message.contains("diarrhea") || message.contains("loose motion")) detectedSymptoms.add("diarrhea");
        if (message.contains("nausea") || message.contains("vomit") || message.contains("throw up")) detectedSymptoms.add("nausea/vomiting");
        if (message.contains("bone") || message.contains("joint") || message.contains("fracture")) detectedSymptoms.add("bone/joint pain");
        if (message.contains("chest") || message.contains("heart pain") || message.contains("breath") || message.contains("shortness")) detectedSymptoms.add("chest/breathing");
        if (message.contains("skin") || message.contains("rash") || message.contains("itch")) detectedSymptoms.add("skin issue");
        if (message.contains("throat") || message.contains("swallow")) detectedSymptoms.add("sore throat");
        if (message.contains("ear") || message.contains("hearing")) detectedSymptoms.add("ear issue");
        if (message.contains("eye") || message.contains("vision")) detectedSymptoms.add("eye issue");
    }

    private boolean hasSymptoms(String message) {
        return message.contains("fever") || message.contains("pain") || message.contains("ache") || 
               message.contains("cough") || message.contains("cold") || message.contains("stomach") ||
               message.contains("throat") || message.contains("rash") || message.contains("breath");
    }

    private String generateFinalGuidance() {
        StringBuilder response = new StringBuilder();
        response.append("Thank you for providing those details.\n\n");
        response.append("**Summary**: You've had ").append(detectedSymptoms.toString())
                .append(" for ").append(duration).append(" (").append(severity).append(").\n\n");

        response.append("### 1. Possible Causes\n");
        response.append("This may be due to: ");
        if (detectedSymptoms.contains("fever") || detectedSymptoms.contains("cough/cold")) {
            response.append("Viral infection, seasonal flu, or common cold. ");
        } else if (detectedSymptoms.contains("headache")) {
            response.append("Dehydration, stress, lack of sleep, or tension. ");
        } else if (detectedSymptoms.contains("stomach ache") || detectedSymptoms.contains("nausea/vomiting")) {
            response.append("Indigestion, food sensitivity, or minor viral gastritis. ");
        } else if (detectedSymptoms.contains("chest/breathing")) {
            response.append("Anxiety, physical exertion, or respiratory issues. ");
        } else {
            response.append("General fatigue or minor physical strain. ");
        }
        response.append("\n\n");

        response.append("### 2. Suggested Home Care\n");
        if (detectedSymptoms.contains("fever")) response.append("• Rest and stay well-hydrated.\n");
        if (detectedSymptoms.contains("cough/cold") || detectedSymptoms.contains("sore throat")) response.append("• Drink warm fluids like herbal tea or honey-lemon water.\n");
        if (detectedSymptoms.contains("headache")) response.append("• Reduce screen time and try to rest in a dark room.\n");
        if (detectedSymptoms.contains("stomach ache") || detectedSymptoms.contains("diarrhea")) response.append("• Stick to a bland diet (rice, toast) and avoid spicy foods.\n");
        response.append("• Monitoring your progress is essential.\n\n");

        response.append("### 3. Recommended Doctor\n");
        response.append("You should consider consulting a: ");
        if (detectedSymptoms.contains("chest/breathing")) response.append("**Cardiologist** or **Pulmonologist** immediately.");
        else if (detectedSymptoms.contains("bone/joint pain")) response.append("**Orthopedic Specialist**.");
        else if (detectedSymptoms.contains("headache")) response.append("**Neurologist**.");
        else if (detectedSymptoms.contains("skin issue")) response.append("**Dermatologist**.");
        else if (detectedSymptoms.contains("eye issue")) response.append("**Ophthalmologist**.");
        else if (detectedSymptoms.contains("ear issue")) response.append("**ENT Specialist**.");
        else response.append("**General Physician**.");
        response.append("\n\n");

        response.append("### ⚠️ WARNING Signs\n");
        response.append("• If symptoms worsen or last more than 3 days, consult a doctor immediately.\n");
        if (detectedSymptoms.contains("chest/breathing")) {
            response.append("• ALERT: Chest pain and breathing difficulty can be serious. Seek ER care now.\n");
        }
        
        response.append("\n**Tip**: You can book an appointment directly through the 'Appointments' section in this app.");

        return response.toString();
    }

    private void addMessage(String message, boolean isUser) {
        // Use the correct constructor for ChatMessageEntity
        ChatMessageEntity chatMessage = new ChatMessageEntity(
                "", // messageId
                isUser ? "user" : "bot", // senderId
                isUser ? "bot" : "user", // receiverId
                message, // text
                String.valueOf(System.currentTimeMillis()) // timestamp
        );
        messageList.add(chatMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}