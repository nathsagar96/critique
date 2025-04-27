package dev.sagar.critique.controllers;

import dev.sagar.critique.domain.dtos.RestaurantCreateUpdateRequestDto;
import dev.sagar.critique.domain.dtos.RestaurantDto;
import dev.sagar.critique.mappers.RestaurantMapper;
import dev.sagar.critique.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

  private final RestaurantService restaurantService;
  private final RestaurantMapper restaurantMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RestaurantDto createRestaurant(
      @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto) {
    return restaurantMapper.toRestaurantDto(
        restaurantService.createRestaurant(
            restaurantMapper.toRestaurantCreateUpdateRequest(requestDto)));
  }
}
