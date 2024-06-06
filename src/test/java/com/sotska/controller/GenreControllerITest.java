package com.sotska.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.sotska.entity.Genre;
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
@DataSet(value = "dataset/movie/dataset_genres.yml",
        cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
@RunWith(SpringRunner.class)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
class GenreControllerITest extends ITest {

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
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

//    @Test
//    @Ignore
//    void shouldGetAllGenresFromCache() throws Exception {
//        var firstResult = getGenres();
//
////todo PostConstruct before fill db
//        var secondResult = getGenres();
//
//        assertEquals(0, firstResult.size());
//        assertEquals(3, secondResult.size());
//        assertThat(List.of(western, horror, drama)).usingRecursiveComparison().ignoringFields("id").isEqualTo(secondResult);
//    }

    private List<Genre> getGenres() throws Exception {
        var json = mockMvc.perform(get("/v1/genres"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(json, VALUE_TYPE);
    }
}
