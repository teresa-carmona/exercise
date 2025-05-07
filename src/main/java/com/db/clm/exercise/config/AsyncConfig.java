package com.db.clm.exercise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    private static final int CONCURRENT_THREADS = 4;
    private static final int MAX_THREADS = 8;
    private static final int QUEUE_CAPACITY = 100;
    private static final String THREAD_NAME = "ExcelProcessing-";

    @Bean(name = "fileProcessingExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CONCURRENT_THREADS);
        executor.setMaxPoolSize(MAX_THREADS);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME);
        executor.initialize();
        return executor;
    }
}

