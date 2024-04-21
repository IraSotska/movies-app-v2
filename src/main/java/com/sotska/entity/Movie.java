package com.sotska.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
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
}
