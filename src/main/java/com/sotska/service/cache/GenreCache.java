package com.sotska.service.cache;

import com.sotska.mapper.GenreMapper;
import com.sotska.repository.GenreRepository;
import com.sotska.web.dto.GenreCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreCache {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private List<GenreCacheDto> cachedGenres;

    @SneakyThrows
    public List<GenreCacheDto> findAll() {
        return new ArrayList<>(cachedGenres);
    }

    @Scheduled(fixedDelayString = "${cache.time-to-live-hours.genre}", initialDelayString = "${cache.time-to-live-hours.genre}",
            timeUnit = TimeUnit.HOURS)
    public void updateData() {
        var genres = genreRepository.findAll();

        cachedGenres = genres.stream().map(genreMapper::toGenreCacheDto).collect(toList());

        log.info("Genres was updated.");
    }
}
