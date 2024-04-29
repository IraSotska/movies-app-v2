package com.sotska.service;

import com.sotska.entity.*;
import com.sotska.exception.MoviesException;
import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.service.cache.SoftReferenceCache;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.CreateMovieResponseDto;
import com.sotska.web.dto.MovieCacheDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sotska.entity.Currency.UAH;
import static com.sotska.exception.MoviesException.ExceptionType.NOT_FOUND;
import static com.sotska.service.MovieEnrichmentService.MovieEnrichType.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CurrencyRateService currencyRateService;
    private final MovieMapper movieMapper;
    private final GenreService genreService;
    private final CountryService countryService;
    private final SoftReferenceCache<Long, MovieCacheDto> movieCache;
    private final MovieEnrichmentService movieEnrichmentService;

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getRandomMovies() {
        return movieRepository.getRandomMovies();
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenres_Id(genreId);
    }

    public MovieCacheDto getById(Long movieId, Currency currency) throws MoviesException {
        var movie = movieCache.getById(movieId).copy();

        movieEnrichmentService.enrichMovie(movie, REVIEWS);

        if (!UAH.equals(currency)) {
            var rate = currencyRateService.getCurrencyRate(currency);
            movie.setPrice(movie.getPrice() / rate);
        }
        return movie;
    }

    @Transactional
    public CreateMovieResponseDto create(CreateMovieRequestDto requestDto) throws MoviesException {
        var movie = movieMapper.toMovie(requestDto);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());

        return movieMapper.toCreateMovieResponseDto(movieRepository.save(movie));
    }

    @Transactional
    public CreateMovieResponseDto update(UpdateMovieRequestDto requestDto, Long id) {
        var existingMovie = movieRepository.findById(id);

        if (existingMovie.isEmpty()) {
            throw new MoviesException(NOT_FOUND, "Id " + id + " not present.");
        }

        var movie = existingMovie.get();
        movieMapper.mergeMovieIntoUpdateMovieRequestDto(requestDto, movie);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());
        movieCache.update(movie.getId(), movieMapper.toMovieCacheDto(movie));

        return movieMapper.toCreateMovieResponseDto(movieRepository.save(movie));
    }

    private void enrichGenresAndCountriesByIds(Movie movie, List<Long> genreIds, List<Long> countryIds) {
        movie.setGenres(genreService.checkIfExistAndGetByIds(genreIds));
        movie.setCountries(countryService.checkIfExistAndGetByIds(countryIds));
    }
}
