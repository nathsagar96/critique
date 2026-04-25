package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Response DTO for review information", example = """
        {
            "id": "review123",
            "restaurantId": "rest456",
            "userId": "user789",
            "userName": "John Doe",
            "content": "The food was excellent and the service was great. Highly recommended!",
            "rating": 5,
            "photos": [
                {
                    "id": "photo1",
                    "caption": "Delicious pasta dish",
                    "uploadedAt": "2023-01-15T10:30:00Z",
                    "uploadedBy": "user789"
                }
            ],
            "createdAt": "2023-01-15T10:30:00Z",
            "lastEditedAt": "2023-01-16T14:20:00Z",
            "canEdit": true
        }""")
public record ReviewResponse(
        @Schema(description = "Unique identifier of the review", example = "review123")
        String id,

        @Schema(description = "ID of the restaurant being reviewed", example = "rest456")
        String restaurantId,

        @Schema(description = "ID of the user who created the review", example = "user789")
        String userId,

        @Schema(description = "Name of the user who created the review", example = "John Doe")
        String userName,

        @Schema(
                description = "Content of the review",
                example = "The food was excellent and the service was great. Highly recommended!")
        String content,

        @Schema(description = "Rating given to the restaurant (1-5)", example = "5")
        Integer rating,

        @Schema(description = "List of photos associated with the review")
        List<PhotoResponse> photos,

        @Schema(description = "Timestamp when the review was created", example = "2023-01-15T10:30:00Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the review was last edited", example = "2023-01-16T14:20:00Z")
        Instant lastEditedAt,

        @Schema(description = "Whether the current user can edit this review", example = "true")
        Boolean canEdit) {}
