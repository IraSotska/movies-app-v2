package com.sotska.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequestDto {

    private String nameUkrainian;
    private String nameNative;
    private String picturePath;

    @NonNull
    private List<Long> countryIds;

    @NonNull
    private List<Long> genreIds;
}
