package com.sotska.controller;

import org.assertj.core.util.Files;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.net.URISyntaxException;

public abstract class ITest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    public String contentOf(String fileName) throws URISyntaxException {
        return Files.contentOf(
                new File(getClass().getClassLoader().getResource(fileName).toURI()), "UTF-8");
    }
}
