package com.sotska.web.dto;

import com.sotska.entity.Country;
import com.sotska.entity.Genre;
import com.sotska.entity.Review;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCacheDto {

    private Long id;
    private String nameUkrainian;
    private String nameNative;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;
    private List<Country> countries;
    private List<Review> reviews;
    private List<Genre> genres;
}
