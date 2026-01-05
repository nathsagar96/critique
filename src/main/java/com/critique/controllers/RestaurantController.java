package com.critique.controllers;

import com.critique.dtos.requests.CreateRestaurantRequest;
import com.critique.dtos.requests.RestaurantSearchRequest;
import com.critique.dtos.requests.UpdateRestaurantRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.RestaurantResponse;
import com.critique.dtos.responses.RestaurantSummaryResponse;
import com.critique.services.RestaurantSearchService;
import com.critique.services.RestaurantService;
import com.critique.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantSearchService searchService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<PageResponse<RestaurantSummaryResponse>> searchRestaurants(
            @Valid @ModelAttribute RestaurantSearchRequest searchRequest) {
        PageResponse<RestaurantSummaryResponse> response = searchService.searchRestaurants(searchRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        String userId = securityUtils.getCurrentUserId();

        RestaurantResponse response = restaurantService.createRestaurant(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable String restaurantId) {
        RestaurantResponse response = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable String restaurantId, @Valid @RequestBody UpdateRestaurantRequest request) {
        String userId = securityUtils.getCurrentUserId();

        RestaurantResponse response = restaurantService.updateRestaurant(restaurantId, request, userId);

        return ResponseEntity.ok(response);
    }
}
