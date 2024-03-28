package com.sotska.config;

import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.service.cache.SoftReferenceCache;
import com.sotska.web.dto.MovieCacheDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(5);
    }

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .build();
    }

    @Bean
    public SoftReferenceCache<Long, MovieCacheDto> movieCache() {
        return new SoftReferenceCache<>((id) -> movieMapper.toMovieCacheDto(movieRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new)).copy());
    }
}
