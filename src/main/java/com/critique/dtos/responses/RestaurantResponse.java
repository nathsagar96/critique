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
            },
            "operatingHours": {
                "monday": {
                    "openTime": "09:00:00",
                    "closeTime": "21:00:00",
                    "closed": false
                },
                "tuesday": {
                    "openTime": "09:00:00",
                    "closeTime": "21:00:00",
                    "closed": false
                },
                "wednesday": {
                    "openTime": "09:00:00",
                    "closeTime": "21:00:00",
                    "closed": false
                },
                "thursday": {
                    "openTime": "09:00:00",
                    "closeTime": "21:00:00",
                    "closed": false
                },
                "friday": {
                    "openTime": "09:00:00",
                    "closeTime": "22:00:00",
                    "closed": false
                },
                "saturday": {
                    "openTime": "10:00:00",
                    "closeTime": "22:00:00",
                    "closed": false
                },
                "sunday": {
                    "openTime": "10:00:00",
                    "closeTime": "20:00:00",
                    "closed": false
                }
            },
            "photos": [
                {
                    "id": "photo1",
                    "caption": "Restaurant exterior",
                    "uploadedAt": "2024-01-15T10:30:00Z",
                    "uploadedBy": "user456"
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
