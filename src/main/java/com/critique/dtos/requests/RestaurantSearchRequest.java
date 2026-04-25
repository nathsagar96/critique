package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "Request DTO for restaurant search queries", example = """
        {
            "q": "Italian",
            "latitude": 40.7128,
            "longitude": -74.0060,
            "radius": 5.0,
            "cuisineType": "Italian",
            "minRating": 4.0,
            "page": 1,
            "size": 20
        }""")
public record RestaurantSearchRequest(
        @Schema(description = "Search query text", example = "Italian")
        String q,

        @Schema(description = "Latitude for location-based search", example = "40.7128")
        Double latitude,

        @Schema(description = "Longitude for location-based search", example = "-74.0060")
        Double longitude,

        @Schema(description = "Search radius in kilometers", example = "5.0")
        Double radius,

        @Schema(description = "Cuisine type filter", example = "Italian")
        String cuisineType,

        @Schema(description = "Minimum rating filter (1-5)", example = "4.0")
        @Min(value = 1, message = "Minimum rating must be between 1 and 5")
        @Max(value = 5, message = "Minimum rating must be between 1 and 5")
        Double minRating,

        @Schema(description = "Page number for pagination", example = "1")
        @Min(value = 1, message = "Page must be at least 1")
        Integer page,

        @Schema(description = "Page size for pagination", example = "20")
        @Min(value = 1, message = "Size must be between 1 and 100")
        @Max(value = 100, message = "Size must be between 1 and 100")
        Integer size) {
    public RestaurantSearchRequest {
        page = page != null ? page : 1;
        size = size != null ? size : 20;
    }
}
