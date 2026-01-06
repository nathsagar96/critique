package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Response DTO containing geographic location coordinates",
        example = """
        {
            "latitude": 40.7128,
            "longitude": -74.0060
        }""")
public record GeoLocationResponse(
        @Schema(description = "Latitude coordinate", example = "40.7128") Double latitude,
        @Schema(description = "Longitude coordinate", example = "-74.0060") Double longitude) {}
