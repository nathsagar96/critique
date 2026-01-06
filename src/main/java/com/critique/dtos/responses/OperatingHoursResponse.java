package com.critique.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Response DTO for restaurant operating hours",
        example =
                """
        {
            "monday": {
                "openTime": "09:00:00",
                "closeTime": "21:00:00",
                "closed": false
            },
            "tuesday": {
                "openTime": "09:00:00",
                "closeTime": "21:00:00",
                "closed": false
            },
            "wednesday": {
                "openTime": "09:00:00",
                "closeTime": "21:00:00",
                "closed": false
            },
            "thursday": {
                "openTime": "09:00:00",
                "closeTime": "21:00:00",
                "closed": false
            },
            "friday": {
                "openTime": "09:00:00",
                "closeTime": "22:00:00",
                "closed": false
            },
            "saturday": {
                "openTime": "10:00:00",
                "closeTime": "22:00:00",
                "closed": false
            },
            "sunday": {
                "openTime": "10:00:00",
                "closeTime": "20:00:00",
                "closed": false
            }
        }""")
public record OperatingHoursResponse(
        @Schema(description = "Monday operating hours") TimeRangeResponse monday,
        @Schema(description = "Tuesday operating hours") TimeRangeResponse tuesday,
        @Schema(description = "Wednesday operating hours") TimeRangeResponse wednesday,
        @Schema(description = "Thursday operating hours") TimeRangeResponse thursday,
        @Schema(description = "Friday operating hours") TimeRangeResponse friday,
        @Schema(description = "Saturday operating hours") TimeRangeResponse saturday,
        @Schema(description = "Sunday operating hours") TimeRangeResponse sunday) {}
