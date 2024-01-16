package com.sotska.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieDto {

    private Long id;
    private String nameUkrainian;
    private String nameNative;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;
    private List<Long> countries;
    private List<Long> genres;
}
