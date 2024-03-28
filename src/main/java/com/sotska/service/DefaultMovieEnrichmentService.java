package com.sotska.service;

import com.sotska.entity.Movie;
import com.sotska.web.dto.MovieCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;

@Service
@RequiredArgsConstructor
public class DefaultMovieEnrichmentService implements MovieEnrichmentService {

    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;

    @Override
    public void enrichMovie(MovieCacheDto movie, MovieEnrichType... movieEnrichTypes) {
        var movieId = movie.getId();
        var types = Arrays.stream(movieEnrichTypes).toList();

        if (types.contains(COUNTRIES)) {
            movie.setCountries(countryService.findByMovieId(movieId));
        }
        if (types.contains(REVIEWS)) {
            movie.setReviews(reviewService.findByMovieId(movieId));
        }
        if (types.contains(GENRES)) {
            movie.setGenres(genreService.findByMovieId(movieId));
        }
    }
}
