package com.sotska.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

//@ContextConfiguration(classes = SecurityConfig.class)
public abstract class ITest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//    @Autowired
//    protected SpringSecurity springSecurity;
//    @Autowired
//    protected WebApplicationContext webApplicationContext;

//    @Autowired
//    protected MockMvc mockMvc = webAppContextSetup(webApplicationContext)
//            .apply(SecurityMockMvcConfigurers.springSecurity()) // не работало из-за отсутствия этой строки
//            .build();
}
