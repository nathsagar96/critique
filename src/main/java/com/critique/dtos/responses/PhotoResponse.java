package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(
        description = "Response DTO containing photo information",
        example =
                """
        {
            "id": "photo123",
            "caption": "Delicious pasta dish",
            "uploadedAt": "2024-01-15T10:30:00Z",
            "uploadedBy": "user456"
        }""")
public record PhotoResponse(
        @Schema(description = "Unique identifier of the photo", example = "photo123") String id,
        @Schema(description = "Caption for the photo", example = "Delicious pasta dish") String caption,
        @Schema(description = "Timestamp when the photo was uploaded", example = "2024-01-15T10:30:00Z")
                Instant uploadedAt,
        @Schema(description = "ID of the user who uploaded the photo", example = "user456") String uploadedBy) {}
