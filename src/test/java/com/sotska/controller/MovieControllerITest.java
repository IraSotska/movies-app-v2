package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.sotska.entity.*;
import com.sotska.service.CurrencyRateService;
import org.junit.jupiter.api.Test;

import static com.sotska.entity.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@DataSet(value = {"datasets/movie/dataset_user.yml", "datasets/movie/dataset_reviews.yml",
        "datasets/movie/movie_review.yml", "datasets/movie/dataset_genres.yml", "datasets/movie/dataset_movies.yml",
        "datasets/movie/movie_genre.yml", "datasets/movie/dataset_countries.yml", "datasets/movie/movie_country.yml"},
        cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerITest {

    public static final TypeReference<List<Movie>> LIST_OF_MOVIES_TYPE = new TypeReference<>() {
    };

    private static final Country UKRAINE = Country.builder()
            .id(1L)
            .name("Ukraine")
            .build();

    private static final Country AUSTRIA = Country.builder()
            .id(2L)
            .name("Austria")
            .build();

    private static final Country ITALY = Country.builder()
            .id(3L)
            .name("Italy")
            .build();

    private static final Review review1 = Review.builder()
            .id(1L)
            .movieId(1L)
            .text("review")
            .build();

    private static final Review review2 = Review.builder()
            .id(2L)
            .movieId(1L)
            .text("review2")
            .build();

    private static final Review review3 = Review.builder()
            .id(3L)
            .movieId(1L)
            .text("review3")
            .build();
    private static final String MOVIES_PATH = "/v1/movies";

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
            .reviews(List.of(review1))
            .countries(List.of(ITALY))
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
            .countries(List.of(UKRAINE))
            .reviews(List.of(review3))
            .build();

    private final Movie movie3 = Movie.builder()
            .id(3L)
            .nameUkrainian("movie3")
            .price(1.0)
            .picturePath("http://movieee3")
            .rating(4.2)
            .nameNative("native3")
            .yearOfRelease(1993L)
            .countries(List.of(AUSTRIA))
            .genres(List.of(horror))
            .reviews(List.of(review2))
            .build();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Test
    void shouldGetAllMovies() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?page=0&size=3");

        assertEquals(3, result.size());
        assertThat(List.of(movie1, movie2, movie3)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetMovieById() throws Exception {
        var json = mockMvc.perform(get(MOVIES_PATH + "/2"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(movie2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetMovieByIdInUSDCurrency() throws Exception {

        var currencyRate = currencyRateService.getCurrencyRate(USD);

        var json = mockMvc.perform(get(MOVIES_PATH + "/2")
                .param("currency", "USD"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        movie2.setPrice(movie2.getPrice() / currencyRate);

        assertThat(movie2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetMovieByIdInUAHCurrency() throws Exception {
        var json = mockMvc.perform(get(MOVIES_PATH + "/2")
                .param("currency", "UAH"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(movie2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceDesc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=price&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie2, movie1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceAsc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=price&direction=asc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie2, movie1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingDesc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=rating&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie1, movie2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingAsc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=rating&direction=ASC");
        assertEquals(3, result.size());
        assertThat(List.of(movie3, movie1, movie2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetOneMovie() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?size=1&page=1");

        assertEquals(1, result.size());
        assertThat(movie2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void shouldGetRandomMovies() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/random"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(3, result.size());
    }

    @Test
    void shouldGetMoviesByGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/genre/3"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(1, result.size());
        assertThat(movie2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void shouldGetMoviesByNotExistingGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/genre/4"))
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
