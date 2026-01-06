package com.critique.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.services.ReviewService;
import com.critique.utils.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private final String testUserId = "test-user-id";
    private final String testUserName = "Test User";
    private final String testRestaurantId = "test-restaurant-id";
    private final String testReviewId = "test-review-id";

    @Nested
    @DisplayName("Get Restaurant Reviews Tests")
    class GetRestaurantReviewsTests {

        @Test
        @DisplayName("shouldReturnPageResponse when get restaurant reviews")
        void shouldReturnPageResponseWhenGetRestaurantReviews() throws Exception {
            // Given
            ReviewResponse reviewResponse = new ReviewResponse(
                    testReviewId,
                    testRestaurantId,
                    testUserId,
                    testUserName,
                    "Great food and service!",
                    5,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now(),
                    true);

            PageResponse<ReviewResponse> mockResponse = new PageResponse<>(List.of(reviewResponse), 1, 20, 1L, 1);

            when(reviewService.getRestaurantReviews(anyString(), anyString(), any(Integer.class), any(Integer.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/reviews", testRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("sort", "date,desc")
                            .param("page", "1")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testReviewId))
                    .andExpect(jsonPath("$.content[0].restaurantId").value(testRestaurantId))
                    .andExpect(jsonPath("$.content[0].content").value("Great food and service!"))
                    .andExpect(jsonPath("$.content[0].rating").value(5));
        }
    }

    @Nested
    @DisplayName("Create Review Tests")
    class CreateReviewTests {

        @Test
        @DisplayName("shouldCreateReviewSuccessfully when valid request is provided")
        void shouldCreateReviewSuccessfully() throws Exception {
            // Given
            CreateReviewRequest request =
                    new CreateReviewRequest("Great food and service!", 5, List.of("photo1", "photo2"));

            ReviewResponse expectedResponse = new ReviewResponse(
                    testReviewId,
                    testRestaurantId,
                    testUserId,
                    testUserName,
                    "Great food and service!",
                    5,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now(),
                    true);

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(securityUtils.getCurrentUserName()).thenReturn(testUserName);
            when(reviewService.createReview(anyString(), any(CreateReviewRequest.class), anyString(), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/reviews", testRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testReviewId))
                    .andExpect(jsonPath("$.restaurantId").value(testRestaurantId))
                    .andExpect(jsonPath("$.userId").value(testUserId))
                    .andExpect(jsonPath("$.content").value("Great food and service!"))
                    .andExpect(jsonPath("$.rating").value(5));
        }
    }

    @Nested
    @DisplayName("Update Review Tests")
    class UpdateReviewTests {

        @Test
        @DisplayName("shouldUpdateReviewSuccessfully when user is authorized")
        void shouldUpdateReviewSuccessfullyWhenAuthorized() throws Exception {
            // Given
            UpdateReviewRequest request =
                    new UpdateReviewRequest("Updated review - still great!", 4, List.of("photo3"));

            ReviewResponse expectedResponse = new ReviewResponse(
                    testReviewId,
                    testRestaurantId,
                    testUserId,
                    testUserName,
                    "Updated review - still great!",
                    4,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now(),
                    true);

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(reviewService.updateReview(anyString(), anyString(), any(UpdateReviewRequest.class), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{restaurantId}/reviews/{reviewId}", testRestaurantId, testReviewId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value("Updated review - still great!"))
                    .andExpect(jsonPath("$.rating").value(4));
        }
    }

    @Nested
    @DisplayName("Delete Review Tests")
    class DeleteReviewTests {

        @Test
        @DisplayName("shouldDeleteReviewSuccessfully when user is authorized")
        void shouldDeleteReviewSuccessfullyWhenAuthorized() throws Exception {
            // Given
            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);

            // When & Then
            mockMvc.perform(delete(
                                    "/api/v1/restaurants/{restaurantId}/reviews/{reviewId}",
                                    testRestaurantId,
                                    testReviewId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
