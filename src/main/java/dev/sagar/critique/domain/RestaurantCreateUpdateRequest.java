package dev.sagar.critique.domain;

import dev.sagar.critique.domain.entities.Address;
import dev.sagar.critique.domain.entities.OperatingHours;
import dev.sagar.critique.domain.entities.Photo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateUpdateRequest {
  private String name;
  private String cuisineType;
  private String contactInformation;
  private Address address;
  private OperatingHours operatingHours;
  private List<Photo> photoIds;
}
