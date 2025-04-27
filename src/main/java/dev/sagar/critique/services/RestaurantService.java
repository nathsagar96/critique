package dev.sagar.critique.services;

import dev.sagar.critique.domain.RestaurantCreateUpdateRequest;
import dev.sagar.critique.domain.entities.Restaurant;

public interface RestaurantService {

  Restaurant createRestaurant(RestaurantCreateUpdateRequest request);
}
