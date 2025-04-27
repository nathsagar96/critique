package dev.sagar.critique.mappers;

import dev.sagar.critique.domain.dtos.PhotoDto;
import dev.sagar.critique.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {

  PhotoDto toDto(Photo photo);
}
