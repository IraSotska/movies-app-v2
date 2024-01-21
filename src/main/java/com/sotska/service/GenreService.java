package com.sotska.service;

import com.sotska.entity.Genre;
import com.sotska.exception.MoviesException;
import com.sotska.repository.GenreRepository;
import com.sotska.service.cache.GenreCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sotska.exception.MoviesException.ExceptionType.CHILD_ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreCache genreCache;
    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreCache.findAll();
    }

    public List<Genre> checkIfExistAndGetByIds(List<Long> ids) {
        var genres = genreRepository.findAllByIdIn(ids);
        if (ids.size() != genres.size()) {
            throw new MoviesException(CHILD_ENTITY_NOT_FOUND, "Genre not found by one of ids: " + ids);
        }
        return genres;
    }
}
