package com.sotska.service;

import com.sotska.web.dto.MovieCacheDto;

public interface MovieEnrichmentService {

    void enrichMovie(MovieCacheDto movie, MovieEnrichType... movieEnrichTypes);

    enum MovieEnrichType {
        REVIEWS, GENRES, COUNTRIES
    }
}
