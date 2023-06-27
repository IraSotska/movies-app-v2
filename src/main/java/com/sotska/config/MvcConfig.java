package com.sotska.config;

import com.sotska.interceptor.RequestIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor);
    }
}
