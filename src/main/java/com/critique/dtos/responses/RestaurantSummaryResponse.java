package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary response DTO for restaurant information", example = """
        {
            "id": "rest123",
            "name": "Tasty Bites",
            "cuisineType": "Italian",
            "averageRating": 4.5,
            "totalReviews": 120,
            "address": {
                "streetNumber": "123",
                "streetName": "Main St",
                "unit": "Apt 4B",
                "city": "New York",
                "state": "NY",
                "postalCode": "10001",
                "country": "USA",
                "location": {
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
            }
        }""")
public record RestaurantSummaryResponse(
        @Schema(description = "Unique identifier of the restaurant", example = "rest123")
        String id,

        @Schema(description = "Name of the restaurant", example = "Tasty Bites")
        String name,

        @Schema(description = "Type of cuisine served", example = "Italian")
        String cuisineType,

        @Schema(description = "Average rating of the restaurant", example = "4.5")
        Double averageRating,

        @Schema(description = "Total number of reviews", example = "120")
        Integer totalReviews,

        @Schema(description = "Restaurant address information")
        AddressResponse address) {}
