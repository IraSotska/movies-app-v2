package com.sotska.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private Long id;
    private String nameUkrainian;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;

    @ManyToMany(cascade = {CascadeType.ALL}, targetEntity=Genre.class)
    @JoinTable(name = "movie_genre",
            joinColumns = { @JoinColumn(name = "movie_id") },
            inverseJoinColumns = { @JoinColumn(name = "genre_id") })
    private List<Genre> genres;
}
