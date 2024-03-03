package com.sotska.service;

import com.sotska.entity.Movie;

public interface MovieEnrichmentService {

    void enrichMovie(Movie movie, MovieEnrichType... movieEnrichTypes);

    enum MovieEnrichType {
        REVIEWS, GENRES, COUNTRIES
    }
}
