package com.project.backendjson.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchRunner {

    public CommandLineRunner batchRunner(JobLauncher jobLauncher, Job job) {
        return args -> {
            try {
                JobParameters jobParameters = new JobParameters();

                jobLauncher.run(job, jobParameters);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'exécution du job : " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
