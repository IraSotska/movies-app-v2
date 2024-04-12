package com.sotska.service;

import com.sotska.entity.Genre;
import com.sotska.exception.MoviesException;
import com.sotska.repository.GenreRepository;
import com.sotska.service.cache.GenreCache;
import com.sotska.web.dto.GenreCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sotska.exception.MoviesException.ExceptionType.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreCache genreCache;
    private final GenreRepository genreRepository;

    public List<GenreCacheDto> findAll() {
        return genreCache.findAll();
    }

    public List<Genre> findByMovieId(Long movieId) {
        return genreRepository.findByMovieId(movieId);
    }

    public List<Genre> checkIfExistAndGetByIds(List<Long> ids) {
        var genres = genreRepository.findAllByIdIn(ids);
        if (ids.size() != genres.size()) {
            genres.stream().map(Genre::getId).forEach(ids::remove);
            throw new MoviesException(NOT_FOUND, "Genre not found by ids: " + ids);
        }
        return genres;
    }
}
