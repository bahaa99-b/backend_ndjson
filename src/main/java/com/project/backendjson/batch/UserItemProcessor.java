package com.project.backendjson.batch;

import com.project.backendjson.model.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) throws Exception {
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Email n'est pas valide : " + user.getEmail());
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new Exception("Username n'est pas valide : " + user.getUsername());
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new Exception("Password n'est pas valide, il doit contenir au moins 6 caractères.");
        }

        if (user.getCreatedAt() == null || user.getCreatedAt().isAfter(java.time.LocalDateTime.now())) {
            throw new Exception("La date de création n'est pas valide : " + user.getCreatedAt());
        }

        return user;
    }
}
