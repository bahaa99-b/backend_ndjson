package com.project.backendjson.service;

import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinIOService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinIOService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void uploadFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Vérifier si le bucket existe
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                // Créer le bucket s'il n'existe pas
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Bucket créé : " + bucketName);
            }

            // Télécharger le fichier sur MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getName())
                            .stream(fileInputStream, file.length(), -1)
                            .contentType("application/json")
                            .build()
            );

            System.out.println("Fichier téléchargé sur MinIO : " + file.getName());

        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            System.err.println("Erreur lors du téléchargement du fichier : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
