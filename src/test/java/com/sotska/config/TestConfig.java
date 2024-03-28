package com.sotska.config;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestConfig {

    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(5);
    }
}
