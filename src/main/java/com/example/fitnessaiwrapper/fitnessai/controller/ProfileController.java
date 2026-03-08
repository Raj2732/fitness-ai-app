package com.example.fitnessaiwrapper.fitnessai.controller;

import com.example.fitnessaiwrapper.fitnessai.model.UserProfile;
import com.example.fitnessaiwrapper.fitnessai.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PostMapping
    public String saveProfile(@RequestBody UserProfile profile) {
        userProfileRepository.save(profile);
        return "Profile saved successfully";
    }

    @GetMapping("/{userId}")
    public Optional<UserProfile> getProfile(@PathVariable String userId) {
        return userProfileRepository.findById(userId);
    }
}

