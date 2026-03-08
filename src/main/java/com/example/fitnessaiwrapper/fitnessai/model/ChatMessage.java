package com.example.fitnessaiwrapper.fitnessai.model;

import jakarta.persistence.*; // error canot find symbol
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_message", length = 1000)
    private String userMessage;

    @Column(name = "ai_response", length = 2000)
    private String aiResponse;

    @Column(name = "user_rating")
    private Double userRating;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "context", length = 1000)
    private String context;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String userId, String userMessage, String aiResponse) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public Double getUserRating() { return userRating; }
    public void setUserRating(Double userRating) { this.userRating = userRating; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
