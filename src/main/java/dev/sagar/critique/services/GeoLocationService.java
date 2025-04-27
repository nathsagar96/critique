package dev.sagar.critique.services;

import dev.sagar.critique.domain.GeoLocation;
import dev.sagar.critique.domain.entities.Address;

public interface GeoLocationService {
  GeoLocation geoLocate(Address address);
}
