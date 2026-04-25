package com.critique.services;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.entities.Restaurant;
import com.critique.entities.Review;
import com.critique.exceptions.BusinessException;
import com.critique.exceptions.UnauthorizedException;
import com.critique.mappers.ReviewMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final PhotoService photoService;
    private final RestaurantService restaurantService;

    @Transactional
    public ReviewResponse createReview(
            String restaurantId, CreateReviewRequest request, String userId, String userName) {

        log.info("Creating review for restaurant '{}' by user '{}'", restaurantId, userId);

        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);

        if (restaurant.getReviews() != null) {
            var hasReviewed = restaurant.getReviews().stream()
                    .anyMatch(review -> review.getUserId().equals(userId));

            if (hasReviewed) {
                throw new BusinessException("You have already reviewed this restaurant");
            }
        }

        Review review = reviewMapper.toEntity(request);
        review.setId(UUID.randomUUID().toString());
        review.setRestaurantId(restaurantId);
        review.setUserId(userId);
        review.setUserName(userName);
        review.setCreatedAt(Instant.now());
        review.setLastEditedAt(Instant.now());

        if (request.photoIds() != null && !request.photoIds().isEmpty()) {
            var photos = photoService.getPhotosByIds(request.photoIds());
            review.setPhotos(photos);
        } else {
            review.setPhotos(new ArrayList<>());
        }

        if (restaurant.getReviews() == null) {
            restaurant.setReviews(new ArrayList<>());
        }
        restaurant.getReviews().add(review);

        restaurantService.recalculateAverageRating(restaurant);
        restaurant.setUpdatedAt(Instant.now());

        restaurantService.saveRestaurant(restaurant);

        log.info("Review created with ID: {}", review.getId());
        return reviewMapper.toResponse(review);
    }

    public PageResponse<ReviewResponse> getRestaurantReviews(
            String restaurantId, String sortParam, Integer page, Integer size) {

        log.debug("Fetching reviews for restaurant '{}' with sort: {}", restaurantId, sortParam);

        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);

        if (restaurant.getReviews() == null || restaurant.getReviews().isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0);
        }

        var comparator = parseSortParameter(sortParam);

        List<Review> sortedReviews =
                restaurant.getReviews().stream().sorted(comparator).toList();

        var startIndex = (page - 1) * size;
        var endIndex = Math.min(startIndex + size, sortedReviews.size());

        if (startIndex >= sortedReviews.size()) {
            return new PageResponse<>(
                    List.of(), page, size, sortedReviews.size(), (int) Math.ceil((double) sortedReviews.size() / size));
        }

        var paginatedReviews = sortedReviews.subList(startIndex, endIndex);
        var responses = reviewMapper.toResponseList(paginatedReviews);

        var totalPages = (int) Math.ceil((double) sortedReviews.size() / size);

        return new PageResponse<>(responses, page, size, sortedReviews.size(), totalPages);
    }

    @Transactional
    public ReviewResponse updateReview(
            String restaurantId, String reviewId, UpdateReviewRequest request, String userId) {

        log.info("Updating review '{}' by user '{}'", reviewId, userId);

        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);

        Review review = restaurant.getReviews().stream()
                .filter(r -> r.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        if (!review.canEdit(Instant.now())) {
            throw new BusinessException("Reviews can only be edited within 48 hours of posting");
        }

        var oldRating = review.getRating();

        reviewMapper.updateEntity(request, review);
        review.setLastEditedAt(Instant.now());

        if (request.photoIds() != null) {
            if (request.photoIds().isEmpty()) {
                review.setPhotos(new ArrayList<>());
            } else {
                var photos = photoService.getPhotosByIds(request.photoIds());
                review.setPhotos(photos);
            }
        }

        if (oldRating != request.rating()) {
            restaurantService.recalculateAverageRating(restaurant);
        }

        restaurant.setUpdatedAt(Instant.now());
        restaurantService.saveRestaurant(restaurant);

        log.info("Review '{}' updated successfully", reviewId);
        return reviewMapper.toResponse(review);
    }

    private Comparator<Review> parseSortParameter(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            sortParam = "date,desc";
        }

        String[] parts = sortParam.split(",");
        String field = parts[0];
        String direction = parts.length > 1 ? parts[1] : "desc";

        Comparator<Review> comparator =
                switch (field.toLowerCase()) {
                    case "rating" -> Comparator.comparing(Review::getRating);
                    case "date" -> Comparator.comparing(Review::getCreatedAt);
                    default -> Comparator.comparing(Review::getCreatedAt);
                };

        if ("asc".equalsIgnoreCase(direction)) {
            return comparator;
        } else {
            return comparator.reversed();
        }
    }

    public void deleteReview(String restaurantId, String reviewId, String userId) {

        log.info("Deleting review '{}' by user '{}'", reviewId, userId);

        Restaurant restaurant = restaurantService.getRestaurantEntity(restaurantId);

        Review review = restaurant.getReviews().stream()
                .filter(r -> r.getId().equals(reviewId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        restaurant.getReviews().remove(review);
        restaurantService.recalculateAverageRating(restaurant);

        restaurant.setUpdatedAt(Instant.now());
        restaurantService.saveRestaurant(restaurant);

        log.info("Review '{}' deleted successfully", reviewId);
    }
}
