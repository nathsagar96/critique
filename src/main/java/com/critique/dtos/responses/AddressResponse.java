package com.critique.dtos.responses;

public record AddressResponse(
        String streetNumber,
        String streetName,
        String unit,
        String city,
        String state,
        String postalCode,
        String country,
        GeoLocationResponse location) {}
