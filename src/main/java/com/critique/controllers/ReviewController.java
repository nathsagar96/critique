package com.critique.controllers;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.services.ReviewService;
import com.critique.utils.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponse>> getRestaurantReviews(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "date,desc") String sort,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {

        PageResponse<ReviewResponse> response = reviewService.getRestaurantReviews(restaurantId, sort, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable String restaurantId, @Valid @RequestBody CreateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();
        String userName = securityUtils.getCurrentUserName();

        ReviewResponse response = reviewService.createReview(restaurantId, request, userId, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable String restaurantId,
            @PathVariable String reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();

        ReviewResponse response = reviewService.updateReview(restaurantId, reviewId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String restaurantId, @PathVariable String reviewId) {
        String userId = securityUtils.getCurrentUserId();

        reviewService.deleteReview(restaurantId, reviewId, userId);

        return ResponseEntity.noContent().build();
    }
}
