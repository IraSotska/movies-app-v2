package com.sotska.service;

import com.sotska.repository.MovieRepository;
import com.sotska.entity.Genre;
import com.sotska.entity.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MovieService.class})
class MovieServiceTest {

    @MockBean
    private MovieRepository movieRepository;

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
            .genres(List.of(western))
            .build();

    private final Movie movie2 = Movie.builder()
            .nameUkrainian("movie2")
            .price(2.0)
            .picturePath("http://movieee2")
            .rating(10.6)
            .yearOfRelease(1992L)
            .genres(List.of(drama))
            .build();

    private final Movie movie3 = Movie.builder()
            .nameUkrainian("movie3")
            .price(1.0)
            .picturePath("http://movieee3")
            .rating(4.2)
            .yearOfRelease(1993L)
            .genres(List.of(horror))
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
    void getMoviesByGenre() {
    }
}