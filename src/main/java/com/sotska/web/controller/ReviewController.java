package com.sotska.web.controller;

import com.sotska.entity.Review;
import com.sotska.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public void create(@RequestBody @Valid Review review) {
        log.info("Requested to create review: {}.", review);
        reviewService.create(review);
    }
}
