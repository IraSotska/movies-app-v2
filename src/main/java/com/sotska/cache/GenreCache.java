package com.sotska.cache;

import com.sotska.repository.GenreRepository;
import com.sotska.entity.Genre;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreCache {

    private final GenreRepository genreRepository;
    private List<Genre> cachedGenres;

    public List<Genre> findAll() {
        return cachedGenres;
    }

    @PostConstruct
    @Scheduled(fixedDelayString = "${cache.time-to-live.genre}", initialDelayString = "${cache.time-to-live.genre}",
            timeUnit = TimeUnit.HOURS)
    public void updateData() {
        cachedGenres = genreRepository.findAll();
        log.info("Genres was updated.");
    }
}

