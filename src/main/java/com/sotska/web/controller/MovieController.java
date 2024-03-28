package com.sotska.web.controller;

import com.sotska.entity.Currency;
import com.sotska.entity.Movie;
import com.sotska.exception.MoviesException;
import com.sotska.service.MovieService;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.MovieCacheDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public MovieCacheDto getMovieById(@PathVariable Long movieId, @RequestParam(defaultValue = "UAH") Currency currency) {
        log.info("Requested to get movie by id: {}.", movieId);
        return movieService.getById(movieId, currency);
    }

    @GetMapping("/genre/{genreId}")
    public List<Movie> getMoviesByGenre(@PathVariable Long genreId) {
        log.info("Requested to get all movies by genre id: {}.", genreId);
        return movieService.getMoviesByGenre(genreId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public Movie create(@RequestBody @Valid CreateMovieRequestDto createMovieRequestDto) {
        log.info("Requested to create movie: {}.", createMovieRequestDto);
        return movieService.create(createMovieRequestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public Movie update(@RequestBody @Valid UpdateMovieRequestDto updateMovieRequestDto, @PathVariable Long id) {
        log.info("Requested to update movie: {}.", updateMovieRequestDto);
        return movieService.update(updateMovieRequestDto, id);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MoviesException.class)
    public void handle() {
    }
}
