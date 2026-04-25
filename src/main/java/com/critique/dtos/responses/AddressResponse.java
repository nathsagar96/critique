package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO containing address information", example = """
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
public record AddressResponse(
        @Schema(description = "Street number", example = "123")
        String streetNumber,

        @Schema(description = "Street name", example = "Main St")
        String streetName,

        @Schema(description = "Unit/apartment number", example = "Apt 4B")
        String unit,

        @Schema(description = "City name", example = "New York")
        String city,

        @Schema(description = "State/province", example = "NY")
        String state,

        @Schema(description = "Postal/zip code", example = "10001")
        String postalCode,

        @Schema(description = "Country name", example = "USA")
        String country,

        @Schema(description = "Geographic location coordinates")
        GeoLocationResponse location) {}
