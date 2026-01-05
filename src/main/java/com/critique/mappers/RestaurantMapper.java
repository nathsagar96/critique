package com.critique.mappers;

import com.critique.dtos.requests.*;
import com.critique.dtos.responses.*;
import com.critique.entities.*;
import java.util.List;
import org.mapstruct.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "averageRating", constant = "0.0")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Restaurant toEntity(CreateRestaurantRequest request);

    RestaurantResponse toResponse(Restaurant restaurant);

    RestaurantSummaryResponse toSummaryResponse(Restaurant restaurant);

    List<RestaurantSummaryResponse> toSummaryResponseList(List<Restaurant> restaurants);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateRestaurantRequest request, @MappingTarget Restaurant restaurant);

    Address toAddress(AddressRequest request);

    AddressResponse toAddressResponse(Address address);

    GeoPoint toGeoPoint(GeoLocationRequest request);

    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoLocationResponse toGeoLocationResponse(GeoPoint geoPoint);

    OperatingHours toOperatingHours(OperatingHoursRequest request);

    OperatingHoursResponse toOperatingHoursResponse(OperatingHours hours);

    TimeRange toTimeRange(TimeRangeRequest request);

    TimeRangeResponse toTimeRangeResponse(TimeRange range);
}
