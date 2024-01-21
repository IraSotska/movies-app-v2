package com.sotska.service;

import com.sotska.entity.Currency;
import com.sotska.exception.MoviesException;
import com.sotska.repository.MovieRepository;
import com.sotska.entity.Movie;
import com.sotska.web.dto.CreateMovieRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sotska.entity.Currency.UAH;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CurrencyRateService currencyRateService;
    private final GenreService genreService;
    private final CountryService countryService;
    private final ModelMapper modelMapper;

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getRandomMovies() {
        return movieRepository.getRandomMovies();
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenres(genreId);
    }

    public Movie getById(Long movieId, Currency currency) {
        var movie = movieRepository.findById(movieId).orElseThrow(EntityNotFoundException::new);
        if (!UAH.equals(currency)) {
            var rate = currencyRateService.getCurrencyRate(currency);
            movie.setPrice(movie.getPrice() / rate);
        }
        return movie;
    }

    public Movie create(CreateMovieRequestDto createMovieRequestDto) throws MoviesException {

        var genres = genreService.checkIfExistAndGetByIds(createMovieRequestDto.getGenreIds());
        var countries = countryService.checkIfExistAndGetByIds(createMovieRequestDto.getCountryIds());

        var movie = modelMapper.map(createMovieRequestDto, Movie.class);
        movie.setGenres(genres);
        movie.setCountries(countries);

        return movieRepository.save(movie);
    }
}
