package com.sotska.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequestDto {

    @NotBlank
    private String nameUkrainian;

    @NotBlank
    private String nameNative;
    private Long yearOfRelease;
    private Double price;
    private String picturePath;

    @NonNull
    private List<Long> countryIds;

    @NonNull
    private List<Long> genreIds;
}
