package com.sotska.service;

import com.sotska.repository.GenreRepository;
import com.sotska.util.GenreCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreCache genreCache;

    private GenreService genreService;

    @BeforeAll
    private void init() {
        genreService = new GenreService(genreCache);
    }

    @Test
    void findAll() {

        genreService.findAll();
    }
}