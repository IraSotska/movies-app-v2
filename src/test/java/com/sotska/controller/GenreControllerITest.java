package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.sotska.entity.Genre;
import com.sotska.util.GenreCache;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class GenreControllerITest {

    public static final TypeReference<List<Genre>> VALUE_TYPE = new TypeReference<>() {
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreCache genreCache;

    @Before
    public void setUp() {
        genreCache.updateData();
    }

    @Test
    @DataSet(value = "datasets/movie/dataset_genres.yml", cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldGetAllGenres() throws Exception {
        var result = getGenresByUrl("/genres");

        assertEquals(3, result.size());
        assertThat(List.of(western, horror, drama)).usingRecursiveComparison().ignoringFields("id").isEqualTo(result);
    }

    private List<Genre> getGenresByUrl(String url) throws Exception {
        var json = mockMvc.perform(get(url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(json, VALUE_TYPE);
    }
}
