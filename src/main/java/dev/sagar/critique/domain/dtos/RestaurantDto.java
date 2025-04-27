package dev.sagar.critique.domain.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
  private String id;
  private String name;
  private String cuisineType;
  private String contactInformation;
  private Float averageRating;
  private GeoPointDto geoLocation;
  private AddressDto address;
  private OperatingHoursDto operatingHours;
  @Builder.Default private List<PhotoDto> photos = new ArrayList<>();
  @Builder.Default private List<ReviewDto> reviews = new ArrayList<>();
  private UserDto createdBy;
}
