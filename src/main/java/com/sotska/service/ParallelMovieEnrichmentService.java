package com.sotska.service;

import com.sotska.web.dto.MovieCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.*;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParallelMovieEnrichmentService implements MovieEnrichmentService {

    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;
    private String timeout;

    @Value("${extract-timeout-seconds}")
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @SneakyThrows
    @Override
    public void enrichMovie(MovieCacheDto movie, MovieEnrichType... movieEnrichTypes) {
        var executorService = Executors.newWorkStealingPool();

        log.info("Start parallel enrichment movie with id: {}", movie.getId());
        var movieId = movie.getId();
        var types = Arrays.stream(movieEnrichTypes).toList();
        var start = System.currentTimeMillis();

        if (types.contains(COUNTRIES)) {
            executorService.submit(() -> movie.setCountries(countryService.findByMovieId(movieId)));
        }
        if (types.contains(REVIEWS)) {
            executorService.submit(() -> movie.setReviews(reviewService.findByMovieId(movieId)));
        }
        if (types.contains(GENRES)) {
            executorService.submit(() -> movie.setGenres(genreService.findByMovieId(movieId)));
        }
        var end = System.currentTimeMillis();

        executorService.shutdown();
        executorService.awaitTermination(Long.parseLong(timeout), SECONDS);

        log.info("End parallel enrichment movie with id: {}. Enrichment time: {}", movie.getId(), end - start);
    }
}
