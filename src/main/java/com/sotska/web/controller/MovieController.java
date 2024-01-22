package com.sotska.controller;

import com.sotska.entity.Currency;
import com.sotska.entity.Movie;
import com.sotska.exception.MoviesException;
import com.sotska.service.MovieService;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.sotska.exception.MoviesException.ExceptionType.CHILD_ENTITY_NOT_FOUND;
import static com.sotska.exception.MoviesException.ExceptionType.ENTITY_NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public Page<Movie> findAll(@PageableDefault(value = 5) Pageable pageable) {
        log.info("Requested to get all movies with pageable: {}.", pageable);
        return movieService.findAll(pageable);
    }

    @GetMapping("/random")
    public List<Movie> getRandomMovies() {
        log.info("Requested to get random movies.");
        return movieService.getRandomMovies();
    }

    @GetMapping("/{movieId}")
    public Movie getMovieById(@PathVariable Long movieId, @RequestParam(defaultValue = "UAH") Currency currency) {
        log.info("Requested to get movie by id: {}.", movieId);
        return movieService.getById(movieId, currency);
    }

    @GetMapping("/genre/{genreId}")
    public List<Movie> getMoviesByGenre(@PathVariable Long genreId) {
        log.info("Requested to get all movies by genre id: {}.", genreId);
        return movieService.getMoviesByGenre(genreId);
    }

    @PostMapping
    public Movie create(@RequestBody @Valid CreateMovieRequestDto createMovieRequestDto) {
        log.info("Requested to create movie: {}.", createMovieRequestDto);
        try {
            return movieService.create(createMovieRequestDto);
        } catch (MoviesException e) {
            if (CHILD_ENTITY_NOT_FOUND.equals(e.getExceptionType())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Movie update(@RequestBody @Valid UpdateMovieRequestDto updateMovieRequestDto, @PathVariable Long id) {
        log.info("Requested to update movie: {}.", updateMovieRequestDto);
        try {
            return movieService.update(updateMovieRequestDto, id);
        } catch (MoviesException e) {
            if (ENTITY_NOT_FOUND.equals(e.getExceptionType())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
