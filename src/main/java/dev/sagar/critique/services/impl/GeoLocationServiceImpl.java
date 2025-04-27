package dev.sagar.critique.services.impl;

import dev.sagar.critique.domain.GeoLocation;
import dev.sagar.critique.domain.entities.Address;
import dev.sagar.critique.services.GeoLocationService;
import java.util.Random;

public class GeoLocationServiceImpl implements GeoLocationService {

  private static final float MIN_LATITUDE = 51.28f;
  private static final float MAX_LATITUDE = 51.68f;
  private static final float MIN_LONGITUDE = -0.489f;
  private static final float MAX_LONGITUDE = 0.236f;

  @Override
  public GeoLocation geoLocate(Address address) {
    Random random = new Random();

    double latitude = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble();
    double longitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble();

    return GeoLocation.builder().latitude(latitude).longitude(longitude).build();
  }
}
