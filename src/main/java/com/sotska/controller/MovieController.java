package com.sotska.controller;

import com.sotska.entity.Movie;
import com.sotska.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/movie")
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

    @GetMapping("/genre/{genreId}")
    public List<Movie> getMoviesByGenre(@PathVariable Long genreId) {
        log.info("Requested to get all movies by genre id: {}.", genreId);
        return movieService.getMoviesByGenre(genreId);
    }
}
