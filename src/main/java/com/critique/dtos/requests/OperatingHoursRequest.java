package com.critique.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for restaurant operating hours", example = """
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
public record OperatingHoursRequest(
        @Schema(description = "Monday operating hours") TimeRangeRequest monday,
        @Schema(description = "Tuesday operating hours") TimeRangeRequest tuesday,
        @Schema(description = "Wednesday operating hours") TimeRangeRequest wednesday,
        @Schema(description = "Thursday operating hours") TimeRangeRequest thursday,
        @Schema(description = "Friday operating hours") TimeRangeRequest friday,
        @Schema(description = "Saturday operating hours") TimeRangeRequest saturday,
        @Schema(description = "Sunday operating hours") TimeRangeRequest sunday) {}
