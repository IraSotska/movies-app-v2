package com.sotska.web.dto;

import com.sotska.entity.Country;
import com.sotska.entity.Genre;
import com.sotska.entity.Review;
import lombok.*;

import java.util.ArrayList;
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

    @SneakyThrows
    public MovieCacheDto copy() {
        var copy = new MovieCacheDto();
        copy.id = id;
        copy.nameUkrainian = nameUkrainian;
        copy.nameNative = nameNative;
        copy.yearOfRelease = yearOfRelease;
        copy.rating = rating;
        copy.price = price;
        copy.picturePath = picturePath;
        List<Country> countriesCopy = new ArrayList<>(countries.size());
        List<Review> reviewsCopy = new ArrayList<>(reviews.size());
        List<Genre> genresCopy = new ArrayList<>(genres.size());
        for (Country country : countries) countriesCopy.add(country.copy());
        for (Review review : reviews) reviewsCopy.add(review.copy());
        for (Genre genre : genres) genresCopy.add(genre.copy());

        copy.genres = genresCopy;
        copy.countries = countriesCopy;
        copy.reviews = reviewsCopy;
        return copy;
    }
}
