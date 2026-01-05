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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtils securityUtils;

    @GetMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getRestaurantReviews(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "date,desc") String sort,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer size) {

        PageResponse<ReviewResponse> response = reviewService.getRestaurantReviews(restaurantId, sort, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/restaurants/{restaurantId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable String restaurantId, @Valid @RequestBody CreateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();
        String userName = securityUtils.getCurrentUserName();

        ReviewResponse response = reviewService.createReview(restaurantId, request, userId, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable String reviewId, @Valid @RequestBody UpdateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();

        ReviewResponse response = reviewService.updateReview(reviewId, request, userId);

        return ResponseEntity.ok(response);
    }
}
