package com.project.backendjson.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendjson.model.User;
import com.project.backendjson.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }


    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Sauvegarder les utilisateurs au format NDJSON
    public void saveUsersToNDJSON(List<User> users, File outputFile) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (User user : users) {
                String json = objectMapper.writeValueAsString(user);
                writer.write(json + "\n"); // Chaque objet User est écrit sur une ligne
            }
        }
    }
}
