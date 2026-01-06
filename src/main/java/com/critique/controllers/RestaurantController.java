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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "API endpoints for managing restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantSearchService searchService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Search restaurants", description = "Search and filter restaurants based on various criteria")
    public ResponseEntity<PageResponse<RestaurantSummaryResponse>> searchRestaurants(
            @Parameter(description = "Search criteria including name, cuisine type, location, etc.")
                    @Valid
                    @ModelAttribute
                    RestaurantSearchRequest searchRequest) {
        PageResponse<RestaurantSummaryResponse> response = searchService.searchRestaurants(searchRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new restaurant", description = "Create a new restaurant listing")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Parameter(description = "Restaurant creation request with all required details") @Valid @RequestBody
                    CreateRestaurantRequest request) {
        String userId = securityUtils.getCurrentUserId();

        RestaurantResponse response = restaurantService.createRestaurant(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{restaurantId}")
    @Operation(
            summary = "Get restaurant details",
            description = "Retrieve detailed information about a specific restaurant")
    public ResponseEntity<RestaurantResponse> getRestaurant(
            @Parameter(description = "ID of the restaurant to retrieve", example = "rest123") @PathVariable
                    String restaurantId) {
        RestaurantResponse response = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{restaurantId}")
    @Operation(summary = "Update restaurant", description = "Update an existing restaurant listing")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @Parameter(description = "ID of the restaurant to update", example = "rest123") @PathVariable
                    String restaurantId,
            @Parameter(description = "Restaurant update request with modified details") @Valid @RequestBody
                    UpdateRestaurantRequest request) {
        String userId = securityUtils.getCurrentUserId();

        RestaurantResponse response = restaurantService.updateRestaurant(restaurantId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{restaurantId}")
    @Operation(summary = "Delete restaurant", description = "Delete an existing restaurant listing")
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "ID of the restaurant to delete", example = "rest123") @PathVariable
                    String restaurantId) {
        String userId = securityUtils.getCurrentUserId();

        restaurantService.deleteRestaurant(restaurantId, userId);

        return ResponseEntity.noContent().build();
    }
}
