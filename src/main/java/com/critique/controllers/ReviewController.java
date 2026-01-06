package com.critique.controllers;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.services.ReviewService;
import com.critique.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reviews", description = "API endpoints for managing restaurant reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(
            summary = "Get restaurant reviews",
            description = "Retrieve all reviews for a specific restaurant with pagination")
    public ResponseEntity<PageResponse<ReviewResponse>> getRestaurantReviews(
            @Parameter(description = "ID of the restaurant", example = "rest123") @PathVariable String restaurantId,
            @Parameter(description = "Sort criteria (e.g., 'date,desc' or 'rating,asc')", example = "date,desc")
                    @RequestParam(defaultValue = "date,desc")
                    String sort,
            @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") @Min(1)
                    Integer page,
            @Parameter(description = "Number of items per page (1-50)", example = "20")
                    @RequestParam(defaultValue = "20")
                    @Min(1)
                    @Max(50)
                    Integer size) {

        PageResponse<ReviewResponse> response = reviewService.getRestaurantReviews(restaurantId, sort, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new review", description = "Create a review for a specific restaurant")
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "ID of the restaurant being reviewed", example = "rest123") @PathVariable
                    String restaurantId,
            @Parameter(description = "Review creation request with content and rating") @Valid @RequestBody
                    CreateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();
        String userName = securityUtils.getCurrentUserName();

        ReviewResponse response = reviewService.createReview(restaurantId, request, userId, userName);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update a review", description = "Update an existing review for a restaurant")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "ID of the restaurant", example = "rest123") @PathVariable String restaurantId,
            @Parameter(description = "ID of the review to update", example = "review456") @PathVariable String reviewId,
            @Parameter(description = "Review update request with modified content and/or rating") @Valid @RequestBody
                    UpdateReviewRequest request) {

        String userId = securityUtils.getCurrentUserId();

        ReviewResponse response = reviewService.updateReview(restaurantId, reviewId, request, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review", description = "Delete an existing review")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the restaurant", example = "rest123") @PathVariable String restaurantId,
            @Parameter(description = "ID of the review to delete", example = "review456") @PathVariable
                    String reviewId) {
        String userId = securityUtils.getCurrentUserId();

        reviewService.deleteReview(restaurantId, reviewId, userId);

        return ResponseEntity.noContent().build();
    }
}
