package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(
        description = "Response DTO containing restaurant details",
        example =
                """
        {
            "id": "rest123",
            "name": "Tasty Bites",
            "cuisineType": "Italian",
            "description": "Authentic Italian cuisine with a modern twist",
            "phoneNumber": "+1234567890",
            "website": "https://tastybites.com",
            "ownerId": "user456",
            "averageRating": 4.5,
            "totalReviews": 120,
            "address": {
                "street": "123 Main St",
                "city": "New York",
                "state": "NY",
                "postalCode": "10001",
                "country": "USA",
                "geoLocation": {
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
            },
            "operatingHours": {
                "days": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"],
                "timeRanges": [
                    {
                        "startTime": "09:00",
                        "endTime": "22:00"
                    }
                ]
            },
            "photos": [
                {
                    "id": "photo1",
                    "url": "https://example.com/photos/photo1.jpg",
                    "description": "Restaurant exterior"
                }
            ],
            "createdAt": "2024-01-15T10:30:00Z",
            "updatedAt": "2024-01-20T14:45:00Z"
        }""")
public record RestaurantResponse(
        @Schema(description = "Unique identifier of the restaurant", example = "rest123") String id,
        @Schema(description = "Name of the restaurant", example = "Tasty Bites") String name,
        @Schema(description = "Type of cuisine served", example = "Italian") String cuisineType,
        @Schema(
                        description = "Description of the restaurant",
                        example = "Authentic Italian cuisine with a modern twist")
                String description,
        @Schema(description = "Restaurant phone number", example = "+1234567890") String phoneNumber,
        @Schema(description = "Restaurant website URL", example = "https://tastybites.com") String website,
        @Schema(description = "ID of the restaurant owner", example = "user456") String ownerId,
        @Schema(description = "Average rating of the restaurant", example = "4.5") Double averageRating,
        @Schema(description = "Total number of reviews", example = "120") Integer totalReviews,
        @Schema(description = "Restaurant address information") AddressResponse address,
        @Schema(description = "Restaurant operating hours") OperatingHoursResponse operatingHours,
        @Schema(description = "List of photos associated with the restaurant") List<PhotoResponse> photos,
        @Schema(description = "Timestamp when the restaurant was created", example = "2024-01-15T10:30:00Z")
                Instant createdAt,
        @Schema(description = "Timestamp when the restaurant was last updated", example = "2024-01-20T14:45:00Z")
                Instant updatedAt) {}
