package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Request DTO for uploading a photo",
        example = """
        {
            "caption": "Delicious pasta dish"
        }""")
public record PhotoUploadRequest(
        @Schema(description = "Caption for the photo", example = "Delicious pasta dish")
                @Size(max = 500, message = "Caption must not exceed 500 characters")
                String caption) {}
