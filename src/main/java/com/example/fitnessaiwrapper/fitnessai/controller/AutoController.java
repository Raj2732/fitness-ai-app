package com.example.fitnessaiwrapper.fitnessai.controller;

import com.example.fitnessaiwrapper.fitnessai.model.User;
import com.example.fitnessaiwrapper.fitnessai.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AutoController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setUsername(request.getEmail());
        user.setRole("USER");
        user.setTokenLimit(10);
        user.setTokensUsed(0);
        user.setTokenResetDate(LocalDateTime.now().plusDays(1));

        userRepository.save(user);
        return "Signup successful! You can now login.";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null && user.getPassword().equals(request.getPassword())) {
            session.setAttribute("userId", user.getId());

            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setUserId(user.getId().toString());
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setTokenLimit(user.getTokenLimit());
            response.setTokensUsed(user.getTokensUsed());
            return response;
        }

        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage("Invalid email or password");
        return response;
    }

    @GetMapping("/me")
    public LoginResponse getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Not logged in");
            return response;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("User not found");
            return response;
        }

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setUserId(user.getId().toString());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setTokenLimit(user.getTokenLimit());
        response.setTokensUsed(user.getTokensUsed());
        return response;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully";
    }

    // Request/Response DTOs
    static class SignupRequest {
        private String email;
        private String password;
        private String name;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class LoginResponse {
        private boolean success;
        private String message;
        private String userId;
        private String email;
        private String name;
        private String role;
        private int tokenLimit;
        private int tokensUsed;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public int getTokenLimit() { return tokenLimit; }
        public void setTokenLimit(int tokenLimit) { this.tokenLimit = tokenLimit; }
        public int getTokensUsed() { return tokensUsed; }
        public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    }
}
