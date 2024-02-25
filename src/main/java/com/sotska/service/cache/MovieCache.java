package com.sotska.service.cache;

import com.sotska.entity.Movie;
import com.sotska.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieCache {

    private final MovieRepository movieRepository;
    private final Map<Long, SoftReference<Movie>> cachedMovies = new ConcurrentHashMap<>();

    public Movie getById(Long id) {
        if (cachedMovies.containsKey(id)) {
            return cachedMovies.get(id).get();
        }
        var movie = movieRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        log.info("Movie with id: {} added to cache", id);
        cachedMovies.put(id, new SoftReference<>(movie));
        return movie;
    }
}

