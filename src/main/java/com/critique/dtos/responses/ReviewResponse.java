package com.critique.dtos.responses;

import java.time.Instant;
import java.util.List;

public record ReviewResponse(
        String id,
        String restaurantId,
        String userId,
        String userName,
        String content,
        Integer rating,
        List<PhotoResponse> photos,
        Instant createdAt,
        Instant lastEditedAt,
        Boolean canEdit) {}
