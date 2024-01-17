package com.sotska.service;

import com.sotska.repository.MovieRepository;
import com.sotska.entity.Genre;
import com.sotska.entity.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.sotska.entity.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MovieService.class})
class MovieServiceTest {

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private CurrencyRateService currencyRateService;

    @Autowired
    private MovieService movieService;

    private final Genre western = Genre.builder()
            .id(1L)
            .name("western")
            .build();

    private final Genre horror = Genre.builder()
            .id(2L)
            .name("horror")
            .build();

    private final Genre drama = Genre.builder()
            .id(3L)
            .name("drama")
            .build();

    private final Movie movie1 = Movie.builder()
            .nameUkrainian("movie1")
            .price(3.0)
            .picturePath("http://movieee")
            .rating(5.6)
            .yearOfRelease(1991L)
            .genres(List.of(western.getId()))
            .build();

    private final Movie movie2 = Movie.builder()
            .nameUkrainian("movie2")
            .price(2.0)
            .picturePath("http://movieee2")
            .rating(10.6)
            .yearOfRelease(1992L)
            .genres(List.of(drama.getId()))
            .build();

    private final Movie movie3 = Movie.builder()
            .nameUkrainian("movie3")
            .price(1.0)
            .picturePath("http://movieee3")
            .rating(4.2)
            .yearOfRelease(1993L)
            .genres(List.of(horror.getId()))
            .build();

    @Test
    void shouldGetRandomMovies() {
        var movies = List.of(movie1, movie2, movie3);

        when(movieRepository.getRandomMovies()).thenReturn(movies);
        var result = movieService.getRandomMovies();

        assertThat(result).isNotNull().isEqualTo(movies);

        verify(movieRepository).getRandomMovies();
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void shouldGetMovieById() {
        var movieId = 3L;

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie3));

        var result = movieService.getById(movieId, USD);

        assertThat(result).isNotNull().isEqualTo(movie3);

        verify(movieRepository).findById(movieId);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void shouldFindAllMovies() {
        var movies = List.of(movie1, movie2, movie3);
        var pagedResponse = new PageImpl<>(movies);
        var pageable = PageRequest.of(0, 3);

        when(movieRepository.findAll(pageable)).thenReturn(pagedResponse);

        var result = movieService.findAll(pageable);

        assertThat(result).isNotNull().isEqualTo(pagedResponse);

        verify(movieRepository).findAll(pageable);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMoviesByGenre() {
        var movies = List.of(movie2, movie3);
        var genreId = 2L;

        when(movieRepository.findByGenres(genreId)).thenReturn(movies);
        var result = movieService.getMoviesByGenre(genreId);

        assertThat(result).isNotNull().isEqualTo(movies);

        verify(movieRepository).findByGenres(genreId);
        verifyNoMoreInteractions(movieRepository);
    }
}