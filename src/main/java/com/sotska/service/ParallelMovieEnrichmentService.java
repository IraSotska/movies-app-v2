package com.sotska.service;

import com.sotska.web.dto.MovieCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class ParallelMovieEnrichmentService implements MovieEnrichmentService {

    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;
    private final ExecutorService executorService;

    private String timeout;

    @Value("${extract-timeout-seconds}")
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @SneakyThrows
    @Override
    public void enrichMovie(MovieCacheDto movie, MovieEnrichType... movieEnrichTypes) {

        log.info("Start parallel enrichment movie with id: {}", movie.getId());
        var movieId = movie.getId();
        var types = Arrays.stream(movieEnrichTypes).toList();
        var start = System.currentTimeMillis();

        List<Callable<MovieCacheDto>> tasks = new ArrayList<>();

        if (types.contains(COUNTRIES)) {
            tasks.add(() -> {
                var movies = countryService.findByMovieId(movieId);
                if (Thread.currentThread().isInterrupted()) {
                    return movie;
                }
                movie.setCountries(movies);
                return movie;
            });
        }
        if (types.contains(REVIEWS)) {
            tasks.add(() -> {
                movie.setReviews(reviewService.findByMovieId(movieId));
                if (Thread.currentThread().isInterrupted()) {
                    return movie;
                }
                return movie;
            });
        }
        if (types.contains(GENRES)) {
            tasks.add(() -> {
                movie.setGenres(genreService.findByMovieId(movieId));
                if (Thread.currentThread().isInterrupted()) {
                    return movie;
                }
                return movie;
            });
        }

        var end = System.currentTimeMillis();
        executorService.invokeAll(tasks, Long.parseLong(timeout), SECONDS);

        log.info("End parallel enrichment movie with id: {}. Enrichment time: {}", movie.getId(), end - start);
    }
}
