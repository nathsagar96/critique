package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request DTO for address information",
        example =
                """
        {
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
        }""")
public record AddressRequest(
        @Schema(description = "Street number", example = "123") @NotBlank(message = "Street number is required")
                String streetNumber,
        @Schema(description = "Street name", example = "Main St") @NotBlank(message = "Street name is required")
                String streetName,
        @Schema(description = "Unit/apartment number", example = "Apt 4B") String unit,
        @Schema(description = "City name", example = "New York") @NotBlank(message = "City is required") String city,
        @Schema(description = "State/province", example = "NY") @NotBlank(message = "State is required") String state,
        @Schema(description = "Postal/zip code", example = "10001") @NotBlank(message = "Postal code is required")
                String postalCode,
        @Schema(description = "Country name", example = "USA") @NotBlank(message = "Country is required")
                String country,
        @Schema(description = "Geographic location coordinates")
                @NotNull(message = "Location coordinates are required")
                @Valid
                GeoLocationRequest location) {}
