package com.sotska.mapper;


import com.sotska.entity.Genre;
import com.sotska.web.dto.GenreCacheDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreCacheDto toGenreCacheDto(Genre genre);
}
