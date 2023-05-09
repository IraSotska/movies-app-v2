package com.sotska.service;

import com.sotska.entity.Currency;
import com.sotska.repository.MovieRepository;
import com.sotska.entity.Movie;
import lombok.RequiredArgsConstructor;
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
        var movie = movieRepository.findById(movieId);
        if (!UAH.equals(currency)) {
            var rate = currencyRateService.getCurrencyRate(currency);
            movie.setPrice(movie.getPrice() / rate);
        }
        return movie;
    }
}
