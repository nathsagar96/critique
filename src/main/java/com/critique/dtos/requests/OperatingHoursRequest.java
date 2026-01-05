package com.critique.dtos.requests;

public record OperatingHoursRequest(
        TimeRangeRequest monday,
        TimeRangeRequest tuesday,
        TimeRangeRequest wednesday,
        TimeRangeRequest thursday,
        TimeRangeRequest friday,
        TimeRangeRequest saturday,
        TimeRangeRequest sunday) {}
