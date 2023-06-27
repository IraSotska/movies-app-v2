package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotska.repository.GenreRepository;
import com.sotska.entity.Genre;
import com.sotska.cache.GenreCache;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {"classpath:add_genres.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:clear_tables.sql", executionPhase = AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class GenreCacheITest {

    @MockBean
    private GenreRepository genreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreCache genreCache;

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void shouldGetAllGenresFromCache() throws Exception {

        when(genreRepository.findAll()).thenReturn(List.of(western, horror, drama));

        var firstResult = getGenres();
        var secondResult = getGenres();

        assertEquals(3, firstResult.size());
        assertEquals(3, secondResult.size());
        assertThat(List.of(western, horror, drama)).usingRecursiveComparison().ignoringFields("id").isEqualTo(firstResult);

        verify(genreRepository, times(1)).findAll();
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void shouldUpdateGenresInCache() throws Exception {
//        genreCache.setTimeToLive(1);

        when(genreRepository.findAll()).thenReturn(List.of(western, horror, drama)).thenReturn(List.of(western, drama));

        var firstResult = getGenres();
        var secondResult = getGenres();

        assertEquals(3, firstResult.size());
        assertEquals(2, secondResult.size());
        assertThat(List.of(western, horror, drama)).usingRecursiveComparison().ignoringFields("id").isEqualTo(firstResult);
        assertThat(List.of(western, drama)).usingRecursiveComparison().ignoringFields("id").isEqualTo(secondResult);

        verify(genreRepository, times(2)).findAll();
        verifyNoMoreInteractions(genreRepository);
    }

    private List<Genre> getGenres() throws Exception {
        var json = mockMvc.perform(get("/genre"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(json, VALUE_TYPE);
    }
}
