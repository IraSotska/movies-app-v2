package com.sotska.service;

import com.sotska.entity.Genre;
import com.sotska.cache.GenreCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreCache genreCache;

    public List<Genre> findAll() {
        return genreCache.findAll();
    }
}
