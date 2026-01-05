package com.critique.dtos.responses;

public record OperatingHoursResponse(
        TimeRangeResponse monday,
        TimeRangeResponse tuesday,
        TimeRangeResponse wednesday,
        TimeRangeResponse thursday,
        TimeRangeResponse friday,
        TimeRangeResponse saturday,
        TimeRangeResponse sunday) {}
