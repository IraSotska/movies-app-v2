package com.sotska.web.dto;

import com.sotska.entity.Country;
import com.sotska.entity.Genre;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieResponseDto {

    private Long id;
    private String nameUkrainian;
    private String nameNative;
    private Long yearOfRelease;
    private Double rating;
    private Double price;
    private String picturePath;
    private List<Country> countries;
    private List<Genre> genres;
}
