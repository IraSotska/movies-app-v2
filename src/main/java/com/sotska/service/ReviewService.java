package com.sotska.service;

import com.sotska.entity.Review;
import com.sotska.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void create(Review review) {
        reviewRepository.save(review);
    }
}
