package com.sotska.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUkrainian;
    private String nameNative;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;

    @OneToMany(cascade = CascadeType.MERGE, targetEntity = Country.class, fetch = FetchType.EAGER)
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
    public Movie clone() throws CloneNotSupportedException {
        return (Movie) super.clone();
    }
}
