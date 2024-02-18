package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.sotska.entity.*;
import com.sotska.service.CurrencyRateService;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sotska.entity.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@DBRider
@DataSet(value = {"datasets/movie/dataset_user.yml", "datasets/movie/dataset_reviews.yml",
        "datasets/movie/movie_review.yml", "datasets/movie/dataset_genres.yml", "datasets/movie/dataset_movies.yml",
        "datasets/movie/movie_genre.yml", "datasets/movie/dataset_countries.yml", "datasets/movie/movie_country.yml"},
        cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@Profile(value = {"test"})
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerITest extends ITest {

    private static final double FIRST_MOVIE_PRICE = 3.0;
    private static final long FIRST_MOVIE_YEAR = 1991L;
    private static final double FIRST_MOVIE_RATING = 5.6;
    private static final String MOVIES_PATH = "/v1/movies";

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

    private static final Review REVIEW_1 = Review.builder()
            .id(1L)
            .movieId(1L)
            .text("review")
            .build();

    private static final Review REVIEW_2 = Review.builder()
            .id(2L)
            .movieId(1L)
            .text("review2")
            .build();

    private static final Review REVIEW_3 = Review.builder()
            .id(3L)
            .movieId(1L)
            .text("review3")
            .build();

    private static final Genre WESTERN = Genre.builder()
            .id(1L)
            .name("western")
            .build();

    private static final Genre HORROR = Genre.builder()
            .id(2L)
            .name("horror")
            .build();

    private static final Genre DRAMA = Genre.builder()
            .id(3L)
            .name("drama")
            .build();

    private static final Movie MOVIE_1 = Movie.builder()
            .id(1L)
            .nameUkrainian("movie1")
            .price(FIRST_MOVIE_PRICE)
            .picturePath("http://movieee")
            .rating(FIRST_MOVIE_RATING)
            .nameNative("native1")
            .yearOfRelease(FIRST_MOVIE_YEAR)
            .genres(List.of(WESTERN))
            .reviews(List.of(REVIEW_1))
            .countries(List.of(ITALY))
            .build();

    private static final Movie MOVIE_2 = Movie.builder()
            .id(2L)
            .nameUkrainian("movie2")
            .price(2.0)
            .picturePath("http://movieee2")
            .rating(10.6)
            .nameNative("native2")
            .yearOfRelease(1992L)
            .genres(List.of(DRAMA))
            .countries(List.of(UKRAINE))
            .reviews(List.of(REVIEW_3))
            .build();

    private static final Movie MOVIE_3 = Movie.builder()
            .id(3L)
            .nameUkrainian("movie3")
            .price(1.0)
            .picturePath("http://movieee3")
            .rating(4.2)
            .nameNative("native3")
            .yearOfRelease(1993L)
            .countries(List.of(AUSTRIA))
            .genres(List.of(HORROR))
            .reviews(List.of(REVIEW_2))
            .build();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldGetAllMovies() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?page=0&size=3");

        assertEquals(3, result.size());
        assertThat(List.of(MOVIE_1, MOVIE_2, MOVIE_3)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetMovieById() throws Exception {
        var json = mockMvc.perform(get(MOVIES_PATH + "/2"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(MOVIE_2).isNotNull().isEqualTo(result);
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

        assertThat(MOVIE_2).usingRecursiveComparison().ignoringFields("price").isEqualTo(result);
        assertThat(MOVIE_2.getPrice() / currencyRate).isEqualTo(result.getPrice());
    }

    @Test
    void shouldGetMovieByIdInUAHCurrency() throws Exception {
        var json = mockMvc.perform(get(MOVIES_PATH + "/2")
                .param("currency", "UAH"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var result = objectMapper.readValue(json, new TypeReference<Movie>() {
        });

        assertThat(MOVIE_2).isNotNull().isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceDesc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=price&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(MOVIE_3, MOVIE_2, MOVIE_1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByPriceAsc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=price&direction=asc");

        assertEquals(3, result.size());
        assertThat(List.of(MOVIE_3, MOVIE_2, MOVIE_1)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingDesc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=rating&direction=desc");

        assertEquals(3, result.size());
        assertThat(List.of(MOVIE_3, MOVIE_1, MOVIE_2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetAllMoviesSortByRatingAsc() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?sort=rating&direction=ASC");
        assertEquals(3, result.size());
        assertThat(List.of(MOVIE_3, MOVIE_1, MOVIE_2)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    @Test
    void shouldGetOneMovie() throws Exception {
        var result = getMoviesByUrl(MOVIES_PATH + "?size=1&page=1");

        assertEquals(1, result.size());
        assertThat(MOVIE_2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void
    shouldGetRandomMovies() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/random"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(3, result.size());
        for (var movie : result) {
            assertNotNull(movie.getReviews());
            assertFalse(movie.getReviews().isEmpty());
            assertNotNull(movie.getCountries());
            assertFalse(movie.getCountries().isEmpty());
            assertNotNull(movie.getGenres());
            assertFalse(movie.getGenres().isEmpty());
        }
    }

    @Test
    void shouldGetMoviesByGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/genre/3"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(1, result.size());
        assertThat(MOVIE_2).usingRecursiveComparison().ignoringFields("id").isEqualTo(result.get(0));
    }

    @Test
    void shouldGetMoviesByNotExistingGenre() throws Exception {
        var result = objectMapper.readValue(mockMvc.perform(get(MOVIES_PATH + "/genre/4"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), LIST_OF_MOVIES_TYPE);

        assertEquals(0, result.size());
    }

    @Test
    @DataSet(value = {"datasets/movie/dataset_genres.yml", "datasets/movie/dataset_countries.yml"},
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

        var resultMovie = objectMapper.readValue(mockMvc.perform(post(MOVIES_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMovieRequestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Movie.class);

        assertNotNull(resultMovie);
        assertEquals(List.of(WESTERN), resultMovie.getGenres());
        assertEquals(List.of(UKRAINE), resultMovie.getCountries());
    }

    @Test
    @DataSet(value = {"datasets/movie/dataset_genres.yml", "datasets/movie/dataset_countries.yml"},
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
    @DataSet(value = {"datasets/movie/dataset_genres.yml", "datasets/movie/dataset_countries.yml"},
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

        var resultMovie = objectMapper.readValue(mockMvc.perform(put(MOVIES_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMovieRequestDto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Movie.class);

        assertNotNull(resultMovie);
        assertEquals(List.of(WESTERN, DRAMA), resultMovie.getGenres());
        assertEquals(List.of(UKRAINE, AUSTRIA), resultMovie.getCountries());
        assertEquals(List.of(REVIEW_1), resultMovie.getReviews());
        assertEquals(1L, resultMovie.getId());
        assertEquals(newNameUkrainian, resultMovie.getNameUkrainian());
        assertEquals(newNameNative, resultMovie.getNameNative());
        assertEquals(FIRST_MOVIE_PRICE, resultMovie.getPrice());
        assertEquals(FIRST_MOVIE_RATING, resultMovie.getRating());
        assertEquals(newPicturePath, resultMovie.getPicturePath());
        assertEquals(FIRST_MOVIE_YEAR, resultMovie.getYearOfRelease());
    }

    @Test
    @DataSet(value = {"datasets/movie/dataset_genres.yml", "datasets/movie/dataset_countries.yml"},
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
                .andExpect(status().isNotFound());
    }

    private List<Movie> getMoviesByUrl(String url) throws Exception {
        var json = mockMvc.perform(get(url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final ObjectNode node = new ObjectMapper().readValue(json, ObjectNode.class);

        return objectMapper.readValue(node.get("content").toString(), LIST_OF_MOVIES_TYPE);
    }
}
