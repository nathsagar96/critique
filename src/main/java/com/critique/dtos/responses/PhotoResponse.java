package com.critique.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public record PhotoResponse(
        String id,
        String url,
        String caption,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
                Instant uploadedAt,
        String uploadedBy) {}
