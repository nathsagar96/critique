package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(
        description = "Request DTO for time range information",
        example =
                """
        {
            "openTime": "09:00:00",
            "closeTime": "21:00:00",
            "closed": false
        }""")
public record TimeRangeRequest(
        @Schema(description = "Opening time in HH:MM:SS format", example = "09:00:00") LocalTime openTime,
        @Schema(description = "Closing time in HH:MM:SS format", example = "21:00:00") LocalTime closeTime,
        @Schema(description = "Whether the establishment is closed", example = "false") Boolean closed) {}
