package com.sotska.controller;

import com.sotska.entity.Movie;
import com.sotska.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public Page<Movie> findAll(@PageableDefault(value = 5) Pageable pageable) {
        return movieService.findAll(pageable);
    }

    @GetMapping("/random")
    public List<Movie> getRandomMovies() {
        return movieService.findRandomMovies();
    }

    @GetMapping("/genre/{genreId}")
    public List<Movie> getMoviesByGenre(@PathVariable Long genreId) {
        return movieService.getMoviesByGenre(genreId);
    }
}
