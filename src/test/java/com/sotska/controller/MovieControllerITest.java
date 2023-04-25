package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sotska.entity.Genre;
import com.sotska.entity.Movie;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"classpath:add_movies.sql", "classpath:add_genres.sql", "classpath:add_movies_genres.sql",
        "classpath:add_countries.sql", "classpath:add_movie_country.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:clear_tables.sql", executionPhase = AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MovieControllerITest {

    public static final TypeReference<List<Movie>> LIST_OF_MOVIES_TYPE = new TypeReference<>() {
    };

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
            .id(1L)
            .nameUkrainian("movie1")
            .price(3.0)
            .picturePath("http://movieee")
            .rating(5.6)
            .nameNative("native1")
            .yearOfRelease(1991L)
            .genres(List.of(western))
            .build();

    private final Movie movie2 = Movie.builder()
            .id(2L)
            .nameUkrainian("movie2")
            .price(2.0)
            .picturePath("http://movieee2")
            .rating(10.6)
            .nameNative("native2")
            .yearOfRelease(1992L)
            .genres(List.of(drama))
            .build();

    private final Movie movie3 = Movie.builder()
            .id(3L)
            .nameUkrainian("movie3")
            .price(1.0)
            .picturePath("http://movieee3")
            .rating(4.2)
            .nameNative("native3")
            .yearOfRelease(1993L)
            .genres(List.of(horror))
            .build();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllMovies() throws Exception {
        var result = getMoviesByUrl("/movie?page=0&size=3");

        assertEquals(3, result.size());
        assertThat(List.of(movie1, movie2, movie3)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetMovieById() throws Exception {
        var json = mockMvc.perform(get("/movie/2"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(movie2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetMovieByIdInUSDCurrency() throws Exception {
        var json = mockMvc.perform(get("/movie/2")
                .param("currency", "USD"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(movie2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceDesc() throws Exception {
        var result = getMoviesByUrl("/movie?sort=price&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie2, movie1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceAsc() throws Exception {
        var result = getMoviesByUrl("/movie?sort=price&direction=asc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie2, movie1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingDesc() throws Exception {
        var result = getMoviesByUrl("/movie?sort=rating&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie1, movie2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingAsc() throws Exception {
        var result = getMoviesByUrl("/movie?sort=rating&direction=ASC");
        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie1, movie2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetOneMovie() throws Exception {
        var result = getMoviesByUrl("/movie?size=1&page=1");

        assertEquals(1, result.size());
        assertThat(movie2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void shouldGetRandomMovies() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get("/movie/random"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(3, result.size());
    }

    @Test
    void shouldGetMoviesByGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get("/movie/genre/3"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(1, result.size());
        assertThat(movie2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void shouldGetMoviesByNotExistingGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get("/movie/genre/4"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(0, result.size());
    }

    private List<Movie> getMoviesByUrl(String url) throws Exception {
        var json = mockMvc.perform(get(url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);

        return objectMapper.readValue(node.get("content").toString(), LIST_OF_MOVIES_TYPE);
    }
}
