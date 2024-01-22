package com.sotska.service;

import com.sotska.entity.Currency;
import com.sotska.exception.MoviesException;
import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.entity.Movie;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sotska.entity.Currency.UAH;
import static com.sotska.exception.MoviesException.ExceptionType.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CurrencyRateService currencyRateService;
    private final GenreService genreService;
    private final CountryService countryService;
    private final ModelMapper modelMapper;
    private final MovieMapper movieMapper;

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getRandomMovies() {
        return movieRepository.getRandomMovies();
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenres_Id(genreId);
    }

    public Movie getById(Long movieId, Currency currency) {
        var movie = movieRepository.findById(movieId).orElseThrow(EntityNotFoundException::new);

        if (!UAH.equals(currency)) {
            var rate = currencyRateService.getCurrencyRate(currency);
            movie.setPrice(movie.getPrice() / rate);
        }
        return movie;
    }

    public Movie create(CreateMovieRequestDto requestDto) throws MoviesException {
        var movie = modelMapper.map(requestDto, Movie.class);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());

        return movieRepository.save(movie);
    }

    public Movie update(UpdateMovieRequestDto requestDto, Long id) throws MoviesException {
        var existingMovie = movieRepository.findById(id);

        if (existingMovie.isEmpty()) {
            throw new MoviesException(ENTITY_NOT_FOUND, "Id " + id + " not present.");
        }

        var movie = existingMovie.get();
        movieMapper.movieFromDto(requestDto, movie);
        enrichGenresAndCountriesByIds(movie, requestDto.getGenreIds(), requestDto.getCountryIds());

        return movieRepository.save(movie);
    }

    private void enrichGenresAndCountriesByIds(Movie movie, List<Long> genreIds, List<Long> countryIds) {
        movie.setGenres(genreService.checkIfExistAndGetByIds(genreIds));
        movie.setCountries(countryService.checkIfExistAndGetByIds(countryIds));
    }
}
