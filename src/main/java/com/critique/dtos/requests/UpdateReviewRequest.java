package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(
        description = "Request DTO for updating review information",
        example =
                """
        {
            "content": "The food was excellent and the service was great. Highly recommended!",
            "rating": 5,
            "photoIds": ["photo1", "photo2"]
        }""")
public record UpdateReviewRequest(
        @Schema(
                        description = "Content of the review",
                        example = "The food was excellent and the service was great. Highly recommended!")
                @NotBlank(message = "Review content is required")
                @Size(min = 10, max = 5000, message = "Review must be between 10 and 5000 characters")
                String content,
        @Schema(description = "Rating given to the restaurant (1-5)", example = "5")
                @NotNull(message = "Rating is required")
                @Min(value = 1, message = "Rating must be between 1 and 5")
                @Max(value = 5, message = "Rating must be between 1 and 5")
                Integer rating,
        @Schema(description = "List of photo IDs associated with the review", example = "[\"photo1\", \"photo2\"]")
                List<String> photoIds) {}
