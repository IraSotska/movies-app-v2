package com.sotska.service;

import com.sotska.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class ParallelMovieEnrichmentService implements MovieEnrichmentService {

    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;
    private final ExecutorService executorService;

    @Value("${extract.timeout.seconds}")
    private Integer timeout;

    @Override
    public void enrichMovie(Movie movie, MovieEnrichType... movieEnrichTypes) {
        var movieId = movie.getId();
        var types = Arrays.stream(movieEnrichTypes).toList();

        if (types.contains(COUNTRIES)) {
            var getCountriesTaskResult = executorService.submit(() -> countryService.findByMovieId(movieId));
            movie.setCountries(getFromFutureTask(movieId, getCountriesTaskResult));
        }
        if (types.contains(REVIEWS)) {
            var getReviewsTaskResult = executorService.submit(() -> reviewService.findByMovieId(movieId));
            movie.setReviews(getFromFutureTask(movieId, getReviewsTaskResult));
        }
        if (types.contains(GENRES)) {
            var getGenresTaskResult = executorService.submit(() -> genreService.findByMovieId(movieId));
            movie.setGenres(getFromFutureTask(movieId, getGenresTaskResult));
        }
    }

    private <T> List<T> getFromFutureTask(Long movieId, java.util.concurrent.Future<java.util.List<T>> getCountriesTaskResult) {
        try {
            return getCountriesTaskResult.get(timeout, SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            getCountriesTaskResult.cancel(true);
            throw new RuntimeException("Can't get movie by id: " + movieId, e);
        }
    }
}
