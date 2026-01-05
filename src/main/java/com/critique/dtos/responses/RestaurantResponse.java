package com.critique.dtos.responses;

import java.time.Instant;
import java.util.List;

public record RestaurantResponse(
        String id,
        String name,
        String cuisineType,
        String description,
        String phoneNumber,
        String website,
        String ownerId,
        Double averageRating,
        Integer totalReviews,
        AddressResponse address,
        OperatingHoursResponse operatingHours,
        List<PhotoResponse> photos,
        Instant createdAt,
        Instant updatedAt) {}
