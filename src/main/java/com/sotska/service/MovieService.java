package com.sotska.service;

import com.sotska.entity.*;
import com.sotska.exception.MoviesException;
import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.service.cache.MovieCache;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

import static com.sotska.entity.Currency.UAH;
import static com.sotska.exception.MoviesException.ExceptionType.NOT_FOUND;
import static com.sotska.exception.MoviesException.ExceptionType.TIMEOUT;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CurrencyRateService currencyRateService;
    private final GenreService genreService;
    private final CountryService countryService;
    private final ReviewService reviewService;
    private final MovieMapper movieMapper;
    private final ExecutorService executorService;
    private final MovieCache movieCache;

    @Value("${extract.timeout.seconds}")
    private Integer timeout;

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getRandomMovies() {
        return movieRepository.getRandomMovies();
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenres_Id(genreId);
    }

    public Movie getById(Long movieId, Currency currency) throws MoviesException {
        var movie = movieCache.getById(movieId);

        enrichMovieByGenresReviewsCountries(movieId, movie);

        if (!UAH.equals(currency)) {
            var rate = currencyRateService.getCurrencyRate(currency);
            movie.setPrice(movie.getPrice() / rate);
        }
        return movie;
    }

    @Transactional
    public Movie create(CreateMovieRequestDto requestDto) throws MoviesException {
        var movie = new Movie();
        movieMapper.mergeMovieAndDto(requestDto, movie);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());

        return movieRepository.save(movie);
    }

    @Transactional
    public Movie update(UpdateMovieRequestDto requestDto, Long id) throws MoviesException {
        var existingMovie = movieRepository.findById(id);

        if (existingMovie.isEmpty()) {
            throw new MoviesException(NOT_FOUND, "Id " + id + " not present.");
        }

        var movie = existingMovie.get();
        movieMapper.mergeMovieAndDto(requestDto, movie);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());

        return movieRepository.save(movie);
    }

    private void enrichGenresAndCountriesByIds(Movie movie, List<Long> genreIds, List<Long> countryIds) {
        movie.setGenres(genreService.checkIfExistAndGetByIds(genreIds));
        movie.setCountries(countryService.checkIfExistAndGetByIds(countryIds));
    }

    private void enrichMovieByGenresReviewsCountries(Long movieId, Movie movie) {
        var getGenresTaskResult = executorService.submit(() -> genreService.findByMovieId(movieId));
        var getCountriesTaskResult = executorService.submit(() -> countryService.findByMovieId(movieId));
        var getReviewsTaskResult = executorService.submit(() -> reviewService.findByMovieId(movieId));

        try {
            movie.setGenres(getGenresTaskResult.get(timeout, SECONDS));
            movie.setCountries(getCountriesTaskResult.get(timeout, SECONDS));
            movie.setReviews(getReviewsTaskResult.get(timeout, SECONDS));
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            getGenresTaskResult.cancel(true);
            getCountriesTaskResult.cancel(true);
            getReviewsTaskResult.cancel(true);
            throw new MoviesException(TIMEOUT, "Can't get movie by id: " + movieId);
        }
    }
}
