package com.critique.services;

import com.critique.dtos.requests.CreateRestaurantRequest;
import com.critique.dtos.requests.UpdateRestaurantRequest;
import com.critique.dtos.responses.RestaurantResponse;
import com.critique.entities.Photo;
import com.critique.entities.Restaurant;
import com.critique.entities.Review;
import com.critique.exceptions.UnauthorizedException;
import com.critique.mappers.RestaurantMapper;
import com.critique.repositories.RestaurantRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final PhotoService photoService;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request, String userId) {
        log.info("Creating restaurant '{}' for user '{}'", request.name(), userId);

        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setOwnerId(userId);
        restaurant.setCreatedAt(Instant.now());
        restaurant.setUpdatedAt(Instant.now());
        restaurant.setReviews(new ArrayList<>());

        if (request.photoIds() != null && !request.photoIds().isEmpty()) {
            List<Photo> photos = photoService.getPhotosByIds(request.photoIds());
            restaurant.setPhotos(photos);
        } else {
            restaurant.setPhotos(new ArrayList<>());
        }

        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant created with ID: {}", saved.getId());

        return restaurantMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant(String restaurantId) {
        log.debug("Fetching restaurant with ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));

        return restaurantMapper.toResponse(restaurant);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(String restaurantId, UpdateRestaurantRequest request, String userId) {
        log.info("Updating restaurant '{}' by user '{}'", restaurantId, userId);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this restaurant");
        }

        restaurantMapper.updateEntity(request, restaurant);
        restaurant.setUpdatedAt(Instant.now());

        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant '{}' updated successfully", restaurantId);

        return restaurantMapper.toResponse(updated);
    }

    public Restaurant getRestaurantEntity(String restaurantId) {
        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));
    }

    public void recalculateAverageRating(Restaurant restaurant) {
        if (restaurant.getReviews() == null || restaurant.getReviews().isEmpty()) {
            restaurant.setAverageRating(0.0);
            restaurant.setTotalReviews(0);
            return;
        }

        double average = restaurant.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        restaurant.setAverageRating(Math.round(average * 10.0) / 10.0);
        restaurant.setTotalReviews(restaurant.getReviews().size());
    }

    @Transactional
    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(String restaurantId, String userId) {
        log.info("Deleting restaurant '{}' by user '{}'", restaurantId, userId);

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + restaurantId));

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this restaurant");
        }

        restaurantRepository.deleteById(restaurantId);
        log.info("Restaurant '{}' deleted successfully", restaurantId);
    }
}
