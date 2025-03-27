package com.project.backendjson.batch;

import com.project.backendjson.model.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) throws Exception {
        // Validation de l'email
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Email n'est pas valide : " + user.getEmail());
        }

        // Validation du username (par exemple, ne doit pas être null ou vide)
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new Exception("Username n'est pas valide : " + user.getUsername());
        }

        // Validation du password (par exemple, longueur minimale)
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new Exception("Password n'est pas valide, il doit contenir au moins 6 caractères.");
        }

        // Validation de la date de création (par exemple, ne peut pas être dans le futur)
        if (user.getCreatedAt() == null || user.getCreatedAt().isAfter(java.time.LocalDateTime.now())) {
            throw new Exception("La date de création n'est pas valide : " + user.getCreatedAt());
        }

        // Si toutes les validations passent, renvoyer l'utilisateur
        return user;
    }
}
