package com.sotska.service;

import com.sotska.web.dto.MovieCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParallelMovieEnrichmentService implements MovieEnrichmentService {

    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;
    private final ExecutorService executorService;
    private String timeout;

    @Value("${extract.timeout.seconds}")
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @SneakyThrows
    @Override
    public void enrichMovie(MovieCacheDto movie, MovieEnrichType... movieEnrichTypes) {

        log.info("Start parallel enrichment movie with id: " + movie.getId());
        var movieId = movie.getId();
        var types = Arrays.stream(movieEnrichTypes).toList();

        if (types.contains(COUNTRIES)) {
            var getCountriesTaskResult = executorService.submit(() -> countryService.findByMovieId(movieId));
            movie.setCountries(getFromFutureTask(getCountriesTaskResult));
        }
        if (types.contains(REVIEWS)) {
            var getReviewsTaskResult = executorService.submit(() -> reviewService.findByMovieId(movieId));
            movie.setReviews(getFromFutureTask(getReviewsTaskResult));
        }
        if (types.contains(GENRES)) {
            var getGenresTaskResult = executorService.submit(() -> genreService.findByMovieId(movieId));
            movie.setGenres(getFromFutureTask(getGenresTaskResult));
        }
        log.info("End parallel enrichment movie with id: " + movie.getId());
    }

    private <T> List<T> getFromFutureTask(java.util.concurrent.Future<java.util.List<T>> futureTask) {
        try {
            return futureTask.get(Integer.parseInt(timeout), SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            futureTask.cancel(true);
            currentThread().interrupt();
            return null;
        }
    }
}
