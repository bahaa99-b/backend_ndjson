package com.project.backendjson.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backendjson.model.User;
import org.springframework.batch.item.ItemReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserItemReader implements ItemReader<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserItemReader.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Iterator<String> linesIterator;
    private boolean atLeastOne = false;
    private final List<File> ndjsonFiles;

    public UserItemReader(String directoryPath) {
        try {
            Path absolutePath = Paths.get(directoryPath).toAbsolutePath();
            File directory = absolutePath.toFile();
            if (!directory.exists()) {
                throw new RuntimeException("Directory does not exist: " + absolutePath);
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException("Directory is not a directory: " + absolutePath);
            }

            // Récupérer tous les fichiers NDJSON
            ndjsonFiles = Files.list(directory.toPath())
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".ndjson") && file.canRead() && file.length() > 0)
                    .collect(Collectors.toList());

            // Log pour vérifier les fichiers trouvés
            logger.info("Fichiers NDJSON trouvés : {}", ndjsonFiles.size());
            for (File file : ndjsonFiles) {
                logger.info("Fichier trouvé : {}", file.getAbsolutePath()); // Log des fichiers trouvés
            }

            // Lire toutes les lignes des fichiers
            StringBuilder allLines = new StringBuilder();
            for (File file : ndjsonFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    reader.lines().forEach(line -> {
                        allLines.append(line).append("\n");
                        logger.info("Ligne lue : {}", line); // Log de la ligne lue
                    });
                }
            }

            // Initialiser l'itérateur avec toutes les lignes
            linesIterator = allLines.toString().lines().iterator();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture des fichiers NDJSON", e);
        }
    }

    @Override
    public User read() throws Exception {
        if (linesIterator != null && linesIterator.hasNext()) {
            String line = linesIterator.next();
            try {
                User user = objectMapper.readValue(line, User.class);
                atLeastOne = true;
                logger.info("Utilisateur traité : {}", user); // Log de l'utilisateur traité
                return user;
            } catch (Exception e) {
                // Log d'erreur de parsing
                logger.error("Erreur lors de la lecture de l'utilisateur : " + e.getMessage(), e);
            }
        } else {
            logger.info("Aucune ligne restante dans l'itérateur.");
        }
        return null;
    }

    public boolean isAtLeastOne() {
        return atLeastOne;
    }

    public List<File> getNdjsonFiles() {
        return ndjsonFiles;
    }
}
