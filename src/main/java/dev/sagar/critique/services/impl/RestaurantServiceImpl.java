package dev.sagar.critique.services.impl;

import dev.sagar.critique.domain.GeoLocation;
import dev.sagar.critique.domain.RestaurantCreateUpdateRequest;
import dev.sagar.critique.domain.entities.Address;
import dev.sagar.critique.domain.entities.Photo;
import dev.sagar.critique.domain.entities.Restaurant;
import dev.sagar.critique.repositories.RestaurantRepository;
import dev.sagar.critique.services.GeoLocationService;
import dev.sagar.critique.services.RestaurantService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final GeoLocationService geoLocationService;

  @Override
  public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {
    Address address = request.getAddress();

    GeoLocation geoLocation = geoLocationService.geoLocate(address);

    List<Photo> photos =
        request.getPhotoIds().stream()
            .map(
                photo ->
                    Photo.builder().url(photo.getUrl()).uploadDate(photo.getUploadDate()).build())
            .collect(Collectors.toList());

    Restaurant restaurant =
        Restaurant.builder()
            .name(request.getName())
            .cuisineType(request.getCuisineType())
            .contactInformation(request.getContactInformation())
            .address(address)
            .geoLocation(new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude()))
            .operatingHours(request.getOperatingHours())
            .averageRating(0.0f)
            .photos(photos)
            .build();

    return restaurantRepository.save(restaurant);
  }
}
