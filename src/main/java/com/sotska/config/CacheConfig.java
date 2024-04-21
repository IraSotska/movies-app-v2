package com.sotska.config;

import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.service.MovieEnrichmentService;
import com.sotska.service.cache.SoftReferenceCache;
import com.sotska.web.dto.MovieCacheDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.COUNTRIES;
import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.GENRES;

@Configuration
public class CacheConfig {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private MovieEnrichmentService movieEnrichmentService;

    @Bean
    public SoftReferenceCache<Long, MovieCacheDto> movieCache() {
        return new SoftReferenceCache<>((id) -> {
            var movie = movieMapper.toMovieCacheDto(movieRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new));
            movieEnrichmentService.enrichMovie(movie, GENRES, COUNTRIES);
            return movie;
        });
    }
}
