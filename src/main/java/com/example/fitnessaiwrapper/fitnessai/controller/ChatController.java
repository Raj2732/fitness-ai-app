package com.example.fitnessaiwrapper.fitnessai.controller;

import com.example.fitnessaiwrapper.fitnessai.service.FitnessAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class ChatController {

    @Autowired
    private FitnessAiService aiService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Fitness AI Assistant");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("message", "Welcome to your fitness dashboard!");
        return "dashboard";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = aiService.generateResponse(request.getUserId(), request.getMessage());
        return new ChatResponse(response);
    }

    @PostMapping("/api/rate")
    @ResponseBody
    public String rateResponse(@RequestBody RatingRequest request) {
        aiService.rateResponse(request.getMessageId(), request.getRating());
        return "Rating saved";
    }

    // Inner classes for request/response
    static class ChatRequest {
        private String userId;
        private String message;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class ChatResponse {
        private String message;

        public ChatResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class RatingRequest {
        private Long messageId;
        private Double rating;

        public Long getMessageId() { return messageId; }
        public void setMessageId(Long messageId) { this.messageId = messageId; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }
}
