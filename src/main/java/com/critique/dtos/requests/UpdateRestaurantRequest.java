package com.critique.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
        @Size(max = 200, message = "Name must not exceed 200 characters") String name,
        String cuisineType,
        @Size(max = 2000, message = "Description must not exceed 2000 characters") String description,
        @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Invalid phone number") String phoneNumber,
        @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$", message = "Invalid website URL")
                String website,
        @Valid AddressRequest address,
        @Valid OperatingHoursRequest operatingHours) {}
