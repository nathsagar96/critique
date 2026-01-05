package com.critique.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotBlank(message = "Street number is required") String streetNumber,
        @NotBlank(message = "Street name is required") String streetName,
        String unit,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "Postal code is required") String postalCode,
        @NotBlank(message = "Country is required") String country,
        @NotNull(message = "Location coordinates are required") @Valid GeoLocationRequest location) {}
