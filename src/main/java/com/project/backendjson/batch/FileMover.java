package com.project.backendjson.batch;

import com.project.backendjson.service.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileMover extends JobExecutionListenerSupport {

    private static final Logger logger = LoggerFactory.getLogger(FileMover.class);

    private final UserItemReader reader;
    private final MinIOService minioService;

    private static final String PROCESSED_DIRECTORY = "C:/Users/ACER ASPIRE 3/Desktop/Prjt_Omnishore/backendjson/src/main/java/com/project/backendjson/directory_processed";
    private static final String ERROR_DIRECTORY = "C:/Users/ACER ASPIRE 3/Desktop/Prjt_Omnishore/backendjson/src/main/java/com/project/backendjson/directory_error";

    public FileMover(UserItemReader reader, MinIOService minioService) {
        this.reader = reader;
        this.minioService = minioService;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("afterJob called. Job status: {}", jobExecution.getStatus());

        if (reader == null || !reader.isAtLeastOne()) {
            logger.info("No files to process.");
            return;
        }

        try {
            Files.createDirectories(Path.of(PROCESSED_DIRECTORY));
            Files.createDirectories(Path.of(ERROR_DIRECTORY));

            List<File> filesToMove = reader.getNdjsonFiles();
            if (filesToMove.isEmpty()) {
                logger.info("No NDJSON files found to move.");
            }

            for (File file : filesToMove) {
                Path sourcePath = file.toPath();
                logger.info("Found file to move: {}", sourcePath);

                Path targetPath;

                // Déplacez le fichier selon l'état du job
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    targetPath = Path.of(PROCESSED_DIRECTORY, file.getName());
                    logger.info("Job completed successfully, moving file to processed directory.");
                } else {
                    targetPath = Path.of(ERROR_DIRECTORY, file.getName());
                    logger.warn("Job failed, moving file to error directory.");
                }

                try {
                    // Déplacer le fichier
                    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Moved {} to {}", sourcePath, targetPath);

                    // Upload sur MinIO après déplacement
                    if (Files.exists(targetPath)) {
                        minioService.uploadFile(targetPath.toFile());
                        logger.info("Uploaded to MinIO: {}", targetPath);

                        // Supprimer le fichier source après déplacement
                        boolean deleted = Files.deleteIfExists(sourcePath);
                        if (deleted) {
                            logger.info("Deleted file: {}", sourcePath);
                        } else {
                            logger.warn("Failed to delete file: {}", sourcePath);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to move file: {}", sourcePath, e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed while processing the files: {}", e.getMessage(), e);
        }
    }
}
