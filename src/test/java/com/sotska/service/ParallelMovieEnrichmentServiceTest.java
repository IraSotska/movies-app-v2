package com.sotska.service;

import com.sotska.config.TestConfig;
import com.sotska.entity.Country;
import com.sotska.entity.Genre;
import com.sotska.entity.Review;
import com.sotska.web.dto.MovieCacheDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Profile(value = {"unitTest"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, ParallelMovieEnrichmentService.class})
class ParallelMovieEnrichmentServiceTest {

    @Autowired
    private ParallelMovieEnrichmentService parallelMovieEnrichmentService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private CountryService countryService;

    @MockBean
    private GenreService genreService;

    private static final Long movieId = 1L;
    private static final List<Review> REVIEWS = List.of(Review.builder().text("text").id(movieId).movieId(movieId).build());
    private static final List<Country> COUNTRIES = List.of(Country.builder().name("name").id(movieId).build());
    private static final List<Genre> GENRES = List.of(Genre.builder().name("horror").id(movieId).build());

    @Test
    void shouldEnrichMovie() {
        var delay = 1000;
        parallelMovieEnrichmentService.setTimeout("1");

        when(reviewService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(delay);
            return REVIEWS;
        });
        when(countryService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(delay);
            return COUNTRIES;
        });
        when(genreService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(delay);
            return GENRES;
        });

        var movie = MovieCacheDto.builder().id(movieId).build();

        parallelMovieEnrichmentService.enrichMovie(movie, MovieEnrichmentService.MovieEnrichType.COUNTRIES, MovieEnrichmentService.MovieEnrichType.REVIEWS, MovieEnrichmentService.MovieEnrichType.GENRES);

        assertNotNull(movie.getReviews());
        assertNotNull(movie.getCountries());
        assertNotNull(movie.getGenres());

        assertEquals(REVIEWS, movie.getReviews());
        assertEquals(GENRES, movie.getGenres());
        assertEquals(COUNTRIES, movie.getCountries());
    }

    @Test
    void shouldNotEnrichMovie() {
        var longerDelay = 2000;

        parallelMovieEnrichmentService.setTimeout("1");

        when(reviewService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(longerDelay);
            return REVIEWS;
        });
        when(countryService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(longerDelay);
            return COUNTRIES;
        });
        when(genreService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(longerDelay);
            return GENRES;
        });

        var movie = MovieCacheDto.builder().id(movieId).build();

        parallelMovieEnrichmentService.enrichMovie(movie, MovieEnrichmentService.MovieEnrichType.COUNTRIES, MovieEnrichmentService.MovieEnrichType.REVIEWS, MovieEnrichmentService.MovieEnrichType.GENRES);

        assertNull(movie.getReviews());
        assertNull(movie.getCountries());
        assertNull(movie.getGenres());
    }

    @Test
    void shouldPartlyEnrichMovie() {
        var delay = 800;
        var longerDelay = 2000;

        parallelMovieEnrichmentService.setTimeout("1");

        when(reviewService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(delay);
            return REVIEWS;
        });
        when(countryService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(longerDelay);
            return COUNTRIES;
        });
        when(genreService.findByMovieId(movieId)).thenAnswer(invocation -> {
            Thread.sleep(longerDelay);
            return GENRES;
        });

        var movie = MovieCacheDto.builder().id(movieId).build();

        parallelMovieEnrichmentService.enrichMovie(movie, MovieEnrichmentService.MovieEnrichType.COUNTRIES, MovieEnrichmentService.MovieEnrichType.REVIEWS, MovieEnrichmentService.MovieEnrichType.GENRES);

        assertNotNull(movie.getReviews());
        assertNull(movie.getCountries());
        assertNull(movie.getGenres());
        assertEquals(REVIEWS, movie.getReviews());
    }
}