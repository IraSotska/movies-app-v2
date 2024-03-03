package com.sotska.service.cache;

import com.sotska.entity.Movie;
import com.sotska.mapper.MovieMapper;
import com.sotska.repository.MovieRepository;
import com.sotska.web.dto.MovieCacheDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieCache {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final Map<Long, SoftReference<MovieCacheDto>> cachedMovies = new ConcurrentHashMap<>();

    @SneakyThrows
    public Movie getById(Long id) {
        if (cachedMovies.containsKey(id)) {
            var movie = new Movie();
            movieMapper.mergeMovieCacheDtoIntoMovie(movie, cachedMovies.get(id).get());
            return movie;
        }
        var movie = movieRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var movieCacheDto = new MovieCacheDto();
        movieMapper.mergeMovieIntoMovieCacheDto(movieCacheDto, movie);
        log.info("Movie with id: {} added to cache", id);
        cachedMovies.put(id, new SoftReference<>(movieCacheDto));
        return movie.clone();
    }

    public void update(Movie movie) {
        var movieId = movie.getId();

        if (!cachedMovies.containsKey(movieId)) {
            return;
        }
        var movieCacheDto = new MovieCacheDto();
        movieMapper.mergeMovieIntoMovieCacheDto(movieCacheDto, movie);
        cachedMovies.put(movieId, new SoftReference<>(movieCacheDto));
    }
}

