package com.critique.dtos.responses;

import java.time.Instant;

public record PhotoResponse(String id, String url, String caption, Instant uploadedAt, String uploadedBy) {}
