package com.critique.dtos.responses;

public record RestaurantSummaryResponse(
        String id,
        String name,
        String cuisineType,
        Double averageRating,
        Integer totalReviews,
        AddressResponse address) {}
