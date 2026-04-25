package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Generic paginated response DTO")
public record PageResponse<T>(
        @Schema(description = "List of content items") List<T> content,

        @Schema(description = "Current page number", example = "1")
        int page,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of elements across all pages", example = "100")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages) {}
