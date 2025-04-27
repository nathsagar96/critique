package dev.sagar.critique.mappers;

import dev.sagar.critique.domain.RestaurantCreateUpdateRequest;
import dev.sagar.critique.domain.dtos.GeoPointDto;
import dev.sagar.critique.domain.dtos.RestaurantCreateUpdateRequestDto;
import dev.sagar.critique.domain.dtos.RestaurantDto;
import dev.sagar.critique.domain.entities.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {

  RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(
      RestaurantCreateUpdateRequestDto requestDto);

  RestaurantDto toRestaurantDto(Restaurant restaurant);

  @Mapping(target = "latitude", source = "java(geoPoint.getLat())")
  @Mapping(target = "longitude", source = "java(geoPoint.getLon())")
  GeoPointDto toGeoPointDto(GeoPoint geoPoint);
}
