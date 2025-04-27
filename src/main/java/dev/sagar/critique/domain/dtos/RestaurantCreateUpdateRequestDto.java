package dev.sagar.critique.domain.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateUpdateRequestDto {
  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Cuisine type is required")
  private String cuisineType;

  @NotBlank(message = "Contact information is required")
  private String contactInformation;

  @Valid private AddressDto address;

  @Valid private OperatingHoursDto operatingHours;

  @Size(min = 1, message = "At least one photo id is required")
  private List<String> photoIds;
}
