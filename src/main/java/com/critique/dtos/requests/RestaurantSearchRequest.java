package com.critique.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RestaurantSearchRequest(
        String q,
        Double latitude,
        Double longitude,
        Double radius,
        String cuisineType,
        @Min(value = 1, message = "Minimum rating must be between 1 and 5")
                @Max(value = 5, message = "Minimum rating must be between 1 and 5")
                Double minRating,
        @Min(value = 1, message = "Page must be at least 1") Integer page,
        @Min(value = 1, message = "Size must be between 1 and 100")
                @Max(value = 100, message = "Size must be between 1 and 100")
                Integer size) {
    public RestaurantSearchRequest {
        page = page != null ? page : 1;
        size = size != null ? size : 20;
    }
}
