package com.sotska.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUkrainian;
    private String nameNative;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Country.class)
    @JoinTable(name = "movie_country",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "country_id")})
    private List<Country> countries;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Review.class)
    @JoinTable(name = "movie_review",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "review_id")})
    private List<Review> reviews;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Genre.class)
    @JoinTable(name = "movie_genre",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")})
    private List<Genre> genres;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id) &&
                nameUkrainian.equals(movie.nameUkrainian) &&
                yearOfRelease.equals(movie.yearOfRelease) &&
                rating.equals(movie.rating) &&
                price.equals(movie.price) &&
                nameNative.equals(movie.nameNative) &&
                picturePath.equals(movie.picturePath) &&
                genres.equals(movie.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameNative, nameUkrainian, yearOfRelease, rating, price, picturePath, genres);
    }
}
