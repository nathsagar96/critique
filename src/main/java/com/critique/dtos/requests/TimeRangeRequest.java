package com.critique.dtos.requests;

import java.time.LocalTime;

public record TimeRangeRequest(LocalTime openTime, LocalTime closeTime, Boolean closed) {}
