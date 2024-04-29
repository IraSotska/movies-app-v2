package com.sotska.mapper;

import com.sotska.entity.Movie;
import com.sotska.web.dto.CreateMovieRequestDto;
import com.sotska.web.dto.CreateMovieResponseDto;
import com.sotska.web.dto.MovieCacheDto;
import com.sotska.web.dto.UpdateMovieRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeMovieIntoUpdateMovieRequestDto(UpdateMovieRequestDto dto, @MappingTarget Movie movie);

    Movie toMovie(CreateMovieRequestDto dto);

    CreateMovieResponseDto toCreateMovieResponseDto(Movie movie);

    MovieCacheDto toMovieCacheDto(Movie movie);
}
