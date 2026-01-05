package com.critique.dtos.responses;

import java.time.LocalTime;

public record TimeRangeResponse(LocalTime openTime, LocalTime closeTime, Boolean closed) {}
