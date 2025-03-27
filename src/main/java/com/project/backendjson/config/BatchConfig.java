package com.project.backendjson.config;

import com.project.backendjson.batch.FileMover;
import com.project.backendjson.batch.UserItemProcessor;
import com.project.backendjson.batch.UserItemReader;
import com.project.backendjson.model.User;
import com.project.backendjson.repository.UserRepository;
import com.project.backendjson.service.MinIOService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

import java.nio.file.Paths;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);
    private static final String DIRECTORY_PATH = "C:/Users/ACER ASPIRE 3/Desktop/directory_ndjson";

    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final MinIOService minioService;

    @Bean
    public Step processFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        logger.info("Creating processFileStep");
        return new StepBuilder("processFileStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(compositeItemWriter())
                .build();
    }

    @Bean
    public Job processUserJob(JobRepository jobRepository, Step processFileStep, FileMover fileMoveListener) {
        logger.info("Creating processUserJob");
        return new JobBuilder("processUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(fileMoveListener)
                .flow(processFileStep)
                .end()
                .build();
    }

    @Bean
    public UserItemReader reader() {
        logger.info("Initializing UserItemReader with path: {}", DIRECTORY_PATH);
        return new UserItemReader(Paths.get(DIRECTORY_PATH).toString());
    }

    @Bean
    public UserItemProcessor processor() {
        logger.info("Initializing UserItemProcessor");
        return new UserItemProcessor();
    }

    @Bean
    public CompositeItemWriter<User> compositeItemWriter() {
        logger.info("Initializing CompositeItemWriter");
        CompositeItemWriter<User> writer = new CompositeItemWriter<>();
        JpaItemWriter<User> jpaWriter = new JpaItemWriter<>();
        jpaWriter.setEntityManagerFactory(entityManagerFactory);
        writer.setDelegates(List.of(jpaWriter));
        return writer;
    }

    @Bean
    public FileMover fileMove() {
        logger.info("Creating FileMover");
        return new FileMover(reader(), minioService);
    }

}
