package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(
        description = "Request DTO for creating a new restaurant",
        example =
                """
        {
            "name": "Tasty Bites",
            "cuisineType": "Italian",
            "description": "Authentic Italian cuisine with a modern twist",
            "phoneNumber": "+1234567890",
            "website": "https://tastybites.com",
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
            "photoIds": ["photo1", "photo2"]
        }""")
public record CreateRestaurantRequest(
        @Schema(description = "Name of the restaurant", example = "Tasty Bites")
                @NotBlank(message = "Restaurant name is required")
                @Size(max = 200, message = "Name must not exceed 200 characters")
                String name,
        @Schema(description = "Type of cuisine served", example = "Italian")
                @NotBlank(message = "Cuisine type is required")
                String cuisineType,
        @Schema(
                        description = "Description of the restaurant",
                        example = "Authentic Italian cuisine with a modern twist")
                @Size(max = 2000, message = "Description must not exceed 2000 characters")
                String description,
        @Schema(description = "Restaurant phone number", example = "+1234567890")
                @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Invalid phone number")
                String phoneNumber,
        @Schema(description = "Restaurant website URL", example = "https://tastybites.com")
                @Pattern(
                        regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$",
                        message = "Invalid website URL")
                String website,
        @Schema(description = "Restaurant address information") @NotNull(message = "Address is required") @Valid
                AddressRequest address,
        @Schema(description = "Restaurant operating hours") @Valid OperatingHoursRequest operatingHours,
        @Schema(description = "List of photo IDs associated with the restaurant", example = "[\"photo1\", \"photo2\"]")
                List<String> photoIds) {}
