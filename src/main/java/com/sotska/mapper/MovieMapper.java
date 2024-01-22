package com.sotska.mapper;


import com.sotska.entity.Movie;
import com.sotska.web.dto.UpdateMovieRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void movieFromDto(UpdateMovieRequestDto dto, @MappingTarget Movie entity);
}
