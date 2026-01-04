package com.critique.dtos.requests;

import jakarta.validation.constraints.Size;

public record PhotoUploadRequest(@Size(max = 500, message = "Caption must not exceed 500 characters") String caption) {}
