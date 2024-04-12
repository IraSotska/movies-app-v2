package com.sotska.web.controller;

import com.sotska.service.GenreService;
import com.sotska.web.dto.GenreCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreCacheDto> findAll() {
        log.info("Requested to get all genres.");
        return genreService.findAll();
    }
}
