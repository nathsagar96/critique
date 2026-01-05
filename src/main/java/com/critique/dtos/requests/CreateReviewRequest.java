package com.critique.dtos.requests;

import jakarta.validation.constraints.*;
import java.util.List;

public record CreateReviewRequest(
        @NotBlank(message = "Review content is required")
                @Size(min = 10, max = 5000, message = "Review must be between 10 and 5000 characters")
                String content,
        @NotNull(message = "Rating is required")
                @Min(value = 1, message = "Rating must be between 1 and 5")
                @Max(value = 5, message = "Rating must be between 1 and 5")
                Integer rating,
        List<String> photoIds) {}
