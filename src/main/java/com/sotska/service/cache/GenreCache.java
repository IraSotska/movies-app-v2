package com.sotska.service.cache;

import com.sotska.repository.GenreRepository;
import com.sotska.entity.Genre;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreCache {

    private final GenreRepository genreRepository;
    private List<Genre> cachedGenres;

    @SneakyThrows
    public List<Genre> findAll() {
        var genresCopy = new ArrayList<Genre>();
        for (var genre : cachedGenres) {
            genresCopy.add(genre.clone());
        }
        return genresCopy;
    }

    @Scheduled(fixedDelayString = "${cache.time-to-live-hours.genre}", initialDelayString = "${cache.time-to-live-hours.genre}",
            timeUnit = TimeUnit.HOURS)
    public void updateData() {
        cachedGenres = genreRepository.findAll();
        log.info("Genres was updated.");
    }
}

