package com.example.fitnessaiwrapper.fitnessai.service;

import com.example.fitnessaiwrapper.fitnessai.model.ChatMessage;
import com.example.fitnessaiwrapper.fitnessai.model.User;
import com.example.fitnessaiwrapper.fitnessai.model.UserProfile;
import com.example.fitnessaiwrapper.fitnessai.repository.ChatMessageRepository;
import com.example.fitnessaiwrapper.fitnessai.repository.UserProfileRepository;
import com.example.fitnessaiwrapper.fitnessai.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FitnessAiService {

    @Autowired
    private ChatMessageRepository chatRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    // Self-learning knowledge base
    private Map<String, List<String>> responsePatterns = new HashMap<>();
    private Map<String, Integer> patternWeights = new HashMap<>();
    private Map<String, Integer> intentFrequency = new HashMap<>();

    public String generateResponse(String userId, String userMessage) {
        // Get current user from session
        Long currentUserId = (Long) session.getAttribute("userId");
        User user = null;

        if (currentUserId != null) {
            user = userRepository.findById(currentUserId).orElse(null);
        }

        // Check token limit
        if (user != null && !user.canSendMessage()) {
            return "⚠️ You've used all your free messages today! Sign up for unlimited access or wait until tomorrow.";
        }

        // If guest with no session, create temporary guest
        if (user == null) {
            user = new User();
            user.setRole("GUEST");
            user.setTokenLimit(5);
            user = userRepository.save(user);
            session.setAttribute("userId", user.getId());
        }

        // Increment token usage
        user.incrementTokensUsed();
        userRepository.save(user);

        // Check if token reset needed
        if (user.getTokenResetDate() != null && user.getTokenResetDate().isBefore(LocalDateTime.now())) {
            user.resetTokens();
            userRepository.save(user);
        }

        // Analyze message intent
        String intent = analyzeIntent(userMessage);

        // Track intent frequency for learning
        intentFrequency.put(intent, intentFrequency.getOrDefault(intent, 0) + 1);

        // Get user profile if exists
        UserProfile profile = userProfileRepository.findById(userId).orElse(null);

        // Generate personalized response
        String response = generatePersonalizedResponse(intent, userMessage, profile);

        // Learn from this interaction
        learnFromMessage(userMessage, response, intent);

        // Save conversation
        saveConversation(userId, userMessage, response);

        return response;
    }

    private String analyzeIntent(String message) {
        message = message.toLowerCase();

        if (message.contains("workout") || message.contains("exercise") || message.contains("train")) {
            return "workout_advice";
        } else if (message.contains("diet") || message.contains("nutrition") || message.contains("eat") || message.contains("food")) {
            return "nutrition_advice";
        } else if (message.contains("weight loss") || message.contains("lose weight") || message.contains("fat")) {
            return "weight_loss";
        } else if (message.contains("muscle") || message.contains("gain") || message.contains("bulk")) {
            return "muscle_gain";
        } else if (message.contains("motivation") || message.contains("progress") || message.contains("track")) {
            return "motivation";
        } else if (message.contains("hello") || message.contains("hi") || message.contains("hey")) {
            return "greeting";
        } else {
            return "general_fitness";
        }
    }

    private String generatePersonalizedResponse(String intent, String userMessage, UserProfile profile) {
        // Check if we have learned patterns for this intent (70% chance to use learned patterns)
        List<String> patterns = responsePatterns.getOrDefault(intent, new ArrayList<>());

        if (!patterns.isEmpty() && Math.random() > 0.3) {
            return getLearnedResponse(intent, patterns);
        }

        // Generate base response based on intent and profile
        switch (intent) {
            case "greeting":
                return generateGreeting(profile);
            case "workout_advice":
                return generateWorkoutAdvice(userMessage, profile);
            case "nutrition_advice":
                return generateNutritionAdvice(userMessage, profile);
            case "weight_loss":
                return generateWeightLossAdvice(userMessage, profile);
            case "muscle_gain":
                return generateMuscleGainAdvice(userMessage, profile);
            case "motivation":
                return generateMotivationMessage(profile);
            default:
                return generateGeneralAdvice(userMessage, profile);
        }
    }

    private String generateGreeting(UserProfile profile) {
        if (profile != null && profile.getName() != null) {
            return "Hello " + profile.getName() + "! 👋 How can I help with your fitness journey today?";
        }
        return "Hello! 👋 I'm your fitness AI assistant. How can I help you today?";
    }

    private String generateWorkoutAdvice(String userMessage, UserProfile profile) {
        if (profile == null) {
            return "I'd love to help with a workout plan! Could you tell me about your fitness level and goals?";
        }

        StringBuilder advice = new StringBuilder();
        advice.append(String.format("Based on your profile (%s level, goal: %s), here's a workout suggestion:\n\n",
                profile.getFitnessLevel() != null ? profile.getFitnessLevel() : "beginner",
                profile.getFitnessGoal() != null ? profile.getFitnessGoal() : "general fitness"));

        if (profile.getFitnessGoal() != null && profile.getFitnessGoal().toLowerCase().contains("weight loss")) {
            advice.append("1. Cardio: 30 minutes of HIIT\n");
            advice.append("2. Strength: Full body circuit training\n");
            advice.append("3. Core: 15 minutes of ab work\n");
            advice.append("4. Cool down: 10 minutes stretching");
        } else if (profile.getFitnessGoal() != null && profile.getFitnessGoal().toLowerCase().contains("muscle")) {
            advice.append("1. Compound lifts: Squats, Deadlifts, Bench Press\n");
            advice.append("2. Isolation exercises: 3 sets of 8-12 reps\n");
            advice.append("3. Progressive overload is key!\n");
            advice.append("4. Don't forget rest days for muscle recovery");
        } else {
            advice.append("1. Warm up: 5-10 minutes light cardio\n");
            advice.append("2. Strength training: 30 minutes\n");
            advice.append("3. Cardio: 20 minutes\n");
            advice.append("4. Cool down: 5-10 minutes stretching");
        }

        return advice.toString();
    }

    private String generateNutritionAdvice(String userMessage, UserProfile profile) {
        return "Based on your fitness goals, focus on:\n" +
                "• Lean proteins for muscle repair\n" +
                "• Complex carbs for sustained energy\n" +
                "• Healthy fats for hormone balance\n" +
                "• Plenty of water throughout the day\n\n" +
                "Would you like specific meal suggestions?";
    }

    private String generateWeightLossAdvice(String userMessage, UserProfile profile) {
        return "For sustainable weight loss:\n" +
                "• Create a calorie deficit of 300-500 calories/day\n" +
                "• Combine cardio with strength training\n" +
                "• Eat protein-rich foods to preserve muscle\n" +
                "• Get 7-9 hours of sleep for recovery\n\n" +
                "Remember: consistency is more important than perfection!";
    }

    private String generateMuscleGainAdvice(String userMessage, UserProfile profile) {
        return "To build muscle effectively:\n" +
                "• Eat in a slight calorie surplus\n" +
                "• Consume 1.6-2.2g protein per kg bodyweight\n" +
                "• Progressive overload in your training\n" +
                "• Allow 48 hours between training same muscle groups";
    }

    private String generateMotivationMessage(UserProfile profile) {
        return "Stay consistent with your fitness journey! " +
                "Remember why you started and celebrate small victories along the way. " +
                "Every workout brings you closer to your goals! 💪";
    }

    private String generateGeneralAdvice(String userMessage, UserProfile profile) {
        return "I'm here to help with your fitness journey! " +
                "Feel free to ask me about workouts, nutrition, weight management, or motivation. " +
                "What specific aspect of fitness would you like to explore?";
    }

    private void learnFromMessage(String userMessage, String response, String intent) {
        // Store pattern
        responsePatterns.computeIfAbsent(intent, k -> new ArrayList<>()).add(response);

        // Keep only last 50 patterns per intent to manage memory
        if (responsePatterns.get(intent).size() > 50) {
            responsePatterns.get(intent).remove(0);
        }

        // Update weight for this pattern
        String patternKey = intent + ":" + response.hashCode();
        patternWeights.put(patternKey, patternWeights.getOrDefault(patternKey, 0) + 1);
    }

    private String getLearnedResponse(String intent, List<String> patterns) {
        // Weighted random selection based on pattern success
        List<String> weightedPatterns = new ArrayList<>();
        for (String pattern : patterns) {
            String key = intent + ":" + pattern.hashCode();
            int weight = patternWeights.getOrDefault(key, 1);
            for (int i = 0; i < weight; i++) {
                weightedPatterns.add(pattern);
            }
        }

        if (!weightedPatterns.isEmpty()) {
            Random random = new Random();
            return weightedPatterns.get(random.nextInt(weightedPatterns.size()));
        }

        return patterns.get(new Random().nextInt(patterns.size()));
    }

    private void saveConversation(String userId, String userMessage, String response) {
        ChatMessage chat = new ChatMessage();
        chat.setUserId(userId);
        chat.setUserMessage(userMessage);
        chat.setAiResponse(response);
        chat.setTimestamp(LocalDateTime.now());

        chatRepository.save(chat);
    }

    public void rateResponse(Long messageId, Double rating) {
        ChatMessage message = chatRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setUserRating(rating);
            chatRepository.save(message);

            // Update pattern weight based on rating
            String intent = analyzeIntent(message.getUserMessage());
            String key = intent + ":" + message.getAiResponse().hashCode();
            int currentWeight = patternWeights.getOrDefault(key, 1);

            // Increase weight for good ratings, decrease for bad
            if (rating >= 4) {
                patternWeights.put(key, currentWeight + 2);
            } else if (rating <= 2) {
                patternWeights.put(key, Math.max(1, currentWeight - 1));
            }
        }
    }
}