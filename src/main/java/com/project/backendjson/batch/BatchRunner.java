package com.project.backendjson.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchRunner {

    // Suppression de l'annotation @Bean ici
    public CommandLineRunner batchRunner(JobLauncher jobLauncher, Job job) {
        return args -> {
            try {
                // Créer des paramètres pour l'exécution du job
                JobParameters jobParameters = new JobParameters();

                // Lancer le job avec les paramètres
                jobLauncher.run(job, jobParameters);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'exécution du job : " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
