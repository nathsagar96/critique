package com.critique.mappers;

import com.critique.dtos.responses.PhotoResponse;
import com.critique.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {

    PhotoResponse toResponse(Photo photo);
}
