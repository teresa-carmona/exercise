package com.db.clm.exercise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "fileProcessingExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);         // Número de hilos concurrentes
        executor.setMaxPoolSize(8);          // Máximo de hilos si hay mucha carga
        executor.setQueueCapacity(100);      // Cola para tareas pendientes
        executor.setThreadNamePrefix("ExcelProc-");
        executor.initialize();
        return executor;
    }
}

