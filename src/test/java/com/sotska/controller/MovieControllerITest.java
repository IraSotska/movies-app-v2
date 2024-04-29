package com.sotska.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@DBRider
@DataSet(value = {"dataset/movie/dataset_user.yml", "dataset/movie/dataset_reviews.yml",
        "dataset/movie/movie_review.yml", "dataset/movie/dataset_genres.yml", "dataset/movie/dataset_movies.yml",
        "dataset/movie/movie_genre.yml", "dataset/movie/dataset_countries.yml", "dataset/movie/movie_country.yml"},
        cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@Profile(value = {"test"})
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerITest extends ITest {

    private static final String MOVIES_PATH = "/v1/movies";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldGetAllMovies() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?page=0&size=3"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/find_all_response.json")));
    }

    @Test
    void shouldGetMovieById() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "/2"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_by_id_response.json")));
    }

    @Test
    void shouldGetMovieByIdInUSDCurrency() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "/2")
                .param("currency", "USD"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_by_id_USD_response.json")));
    }

    @Test
    void shouldGetMovieByIdInUAHCurrency() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "/2")
                .param("currency", "UAH"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_by_id_UAH_response.json")));
    }

    @Test
    void shouldGetAllMoviesSortByPriceDesc() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?sort=price&direction=desc"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/find_all_sort_desc_response.json")));
    }

    @Test
    void shouldGetAllMoviesSortByPriceAsc() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?sort=price&direction=asc"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/find_all_price_asc_response.json")));
    }

    @Test
    void shouldGetAllMoviesSortByRatingDesc() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?sort=rating&direction=desc"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/find_all_rating_desc_response.json")));
    }

    @Test
    void shouldGetAllMoviesSortByRatingAsc() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?sort=rating&direction=ASC"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/find_all_rating_asc_response.json")));
    }

    @Test
    void shouldGetOneMovie() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "?size=1&page=1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_response.json")));
    }

    @Test
    void shouldGetRandomMovies() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "/random"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_random_response.json")));
    }

    @Test
    void shouldGetMoviesByGenre() throws Exception {
        mockMvc.perform(get(MOVIES_PATH + "/genre/3"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/get_by_genre_response.json")));
    }

    @Test
    void shouldGetMoviesByNotExistingGenre() throws Exception {
        assertEquals("[]", mockMvc.perform(get(MOVIES_PATH + "/genre/4"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DataSet(value = {"dataset/movie/dataset_genres.yml", "dataset/movie/dataset_countries.yml"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldCreateMovie() throws Exception {
        var createMovieRequestDto = CreateMovieRequestDto.builder()
                .nameUkrainian("movie2")
                .price(2.0)
                .picturePath("http://movieee2")
                .nameNative("native2")
                .yearOfRelease(1992L)
                .genreIds(List.of(1L))
                .countryIds(List.of(1L))
                .build();

        mockMvc.perform(post(MOVIES_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMovieRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/create_response.json")));
    }

    @Test
    @DataSet(value = {"dataset/movie/dataset_genres.yml", "dataset/movie/dataset_countries.yml"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldThrowExceptionWhileCreateMovieIfGenreNotExist() throws Exception {
        var createMovieRequestDto = CreateMovieRequestDto.builder()
                .nameUkrainian("movie2")
                .price(2.0)
                .picturePath("http://movieee2")
                .nameNative("native2")
                .yearOfRelease(1992L)
                .genreIds(List.of(7L))
                .countryIds(List.of(1L))
                .build();

        mockMvc.perform(post(MOVIES_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMovieRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = {"dataset/movie/dataset_genres.yml", "dataset/movie/dataset_countries.yml"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldThrowExceptionWhileCreateMovieIfCountryNotExist() throws Exception {
        var createMovieRequestDto = CreateMovieRequestDto.builder()
                .nameUkrainian("movie2")
                .price(2.0)
                .picturePath("http://movieee2")
                .nameNative("native2")
                .yearOfRelease(1992L)
                .genreIds(List.of(1L))
                .countryIds(List.of(1L, 7L))
                .build();

        mockMvc.perform(post(MOVIES_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMovieRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateMovie() throws Exception {

        var newNameUkrainian = "new name Ukrainian";
        var newNameNative = "new name";
        var newPicturePath = "http://newPath";

        var updateMovieRequestDto = UpdateMovieRequestDto.builder()
                .nameUkrainian(newNameUkrainian)
                .picturePath(newPicturePath)
                .nameNative(newNameNative)
                .genreIds(List.of(1L, 3L))
                .countryIds(List.of(1L, 2L))
                .build();

        mockMvc.perform(put(MOVIES_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMovieRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(contentOf("dataset/movie/update_response.json")));
    }

    @Test
    @DataSet(value = {"dataset/movie/dataset_genres.yml", "dataset/movie/dataset_countries.yml"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldThrowExceptionWhileUpdateMovieIfIdNotExist() throws Exception {

        var updateMovieRequestDto = UpdateMovieRequestDto.builder()
                .nameUkrainian("newNameUkrainian")
                .picturePath("newPicturePath")
                .nameNative("newNameNative")
                .genreIds(List.of(1L, 3L))
                .countryIds(List.of(1L, 2L))
                .build();

        mockMvc.perform(put(MOVIES_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMovieRequestDto)))
                .andExpect(status().isBadRequest());
    }
}
