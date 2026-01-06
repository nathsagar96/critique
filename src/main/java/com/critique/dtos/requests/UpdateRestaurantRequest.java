package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request DTO for updating restaurant information",
        example =
                """
        {
            "name": "Tasty Bites Updated",
            "cuisineType": "Italian",
            "description": "Authentic Italian cuisine with a modern twist",
            "phoneNumber": "+1234567890",
            "website": "https://tastybites.com",
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
            }
        }""")
public record UpdateRestaurantRequest(
        @Schema(description = "Name of the restaurant", example = "Tasty Bites Updated")
                @Size(max = 200, message = "Name must not exceed 200 characters")
                String name,
        @Schema(description = "Type of cuisine served", example = "Italian") String cuisineType,
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
        @Schema(description = "Restaurant address information") @Valid AddressRequest address,
        @Schema(description = "Restaurant operating hours") @Valid OperatingHoursRequest operatingHours) {}
