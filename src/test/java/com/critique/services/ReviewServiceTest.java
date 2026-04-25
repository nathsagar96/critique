package com.critique.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.entities.Photo;
import com.critique.entities.Restaurant;
import com.critique.entities.Review;
import com.critique.exceptions.BusinessException;
import com.critique.exceptions.UnauthorizedException;
import com.critique.mappers.ReviewMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private PhotoService photoService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private ReviewService reviewService;

    private final String testUserId = "test-user-id";
    private final String testUserName = "Test User";
    private final String testRestaurantId = "test-restaurant-id";
    private final String testReviewId = "test-review-id";
    private final String testOtherUserId = "other-user-id";

    @Nested
    @DisplayName("Create Review Tests")
    class CreateReviewTests {

        @Test
        @DisplayName("shouldCreateReviewSuccessfully when valid request is provided")
        void shouldCreateReviewSuccessfully() {
            // Given
            var request = new CreateReviewRequest("Great food!", 5, List.of("photo1", "photo2"));
            Restaurant restaurant = createTestRestaurant();
            Review mockReview = createTestReview();
            ReviewResponse expectedResponse = createTestReviewResponse();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(photoService.getPhotosByIds(request.photoIds())).thenReturn(createTestPhotos());
            when(reviewMapper.toEntity(request)).thenReturn(mockReview);
            when(reviewMapper.toResponse(any(Review.class))).thenReturn(expectedResponse);

            // When
            ReviewResponse result = reviewService.createReview(testRestaurantId, request, testUserId, testUserName);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertEquals(expectedResponse.content(), result.content());
            assertEquals(expectedResponse.rating(), result.rating());
            assertEquals(expectedResponse.userId(), result.userId());
            assertEquals(expectedResponse.userName(), result.userName());
            verify(restaurantService, times(1)).saveRestaurant(any(Restaurant.class));
            verify(photoService, times(1)).getPhotosByIds(request.photoIds());
        }

        @Test
        @DisplayName("shouldCreateReviewSuccessfully when no photos are provided")
        void shouldCreateReviewSuccessfullyWithoutPhotos() {
            // Given
            var request = new CreateReviewRequest("Great food!", 5, null);
            Restaurant restaurant = createTestRestaurant();
            Review mockReview = createTestReview();
            ReviewResponse expectedResponse = createTestReviewResponse();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(reviewMapper.toEntity(request)).thenReturn(mockReview);
            when(reviewMapper.toResponse(any(Review.class))).thenReturn(expectedResponse);

            // When
            ReviewResponse result = reviewService.createReview(testRestaurantId, request, testUserId, testUserName);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            verify(restaurantService, times(1)).saveRestaurant(any(Restaurant.class));
            verify(photoService, never()).getPhotosByIds(any());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when user has already reviewed the restaurant")
        void shouldThrowBusinessExceptionWhenUserAlreadyReviewed() {
            // Given
            var request = new CreateReviewRequest("Great food!", 5, null);
            Restaurant restaurant = createTestRestaurantWithExistingReview();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> reviewService.createReview(testRestaurantId, request, testUserId, testUserName));

            assertEquals("You have already reviewed this restaurant", exception.getMessage());
            verify(restaurantService, never()).saveRestaurant(any());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when restaurant does not exist")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist() {
            // Given
            var request = new CreateReviewRequest("Great food!", 5, null);

            when(restaurantService.getRestaurantEntity(testRestaurantId))
                    .thenThrow(new ResourceNotFoundException("Restaurant not found with id " + testRestaurantId));

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> reviewService.createReview(testRestaurantId, request, testUserId, testUserName));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Restaurant Reviews Tests")
    class GetRestaurantReviewsTests {

        @Test
        @DisplayName("shouldReturnEmptyPageResponse when restaurant has no reviews")
        void shouldReturnEmptyPageResponseWhenNoReviews() {
            // Given
            Restaurant restaurant = createTestRestaurant();
            restaurant.setReviews(new ArrayList<>());

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When
            var result = reviewService.getRestaurantReviews(testRestaurantId, null, 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());
            assertEquals(0, result.totalPages());
        }

        @Test
        @DisplayName("shouldReturnPaginatedReviews when restaurant has reviews")
        void shouldReturnPaginatedReviews() {
            // Given
            Restaurant restaurant = createTestRestaurantWithReviews();
            // Mock to return responses based on actual input list (respecting pagination)
            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(reviewMapper.toResponseList(anyList())).thenAnswer(invocation -> {
                List<Review> reviews = invocation.getArgument(0);
                return reviews.stream()
                        .map(r -> new ReviewResponse(
                                r.getId(),
                                r.getRestaurantId(),
                                r.getUserId(),
                                r.getUserName(),
                                r.getContent(),
                                r.getRating(),
                                List.of(),
                                r.getCreatedAt(),
                                r.getLastEditedAt(),
                                true))
                        .toList();
            });

            // When
            var result = reviewService.getRestaurantReviews(testRestaurantId, "date,desc", 1, 2);

            // Then
            assertNotNull(result);
            assertEquals(2, result.content().size());
            assertEquals(3, result.totalElements());
            assertEquals(2, result.totalPages());
            assertEquals(1, result.page());
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("shouldReturnSortedReviewsByRating when sort parameter is rating")
        void shouldReturnSortedReviewsByRating() {
            // Given
            Restaurant restaurant = createTestRestaurantWithReviews();
            // Mock to return responses based on actual input list (respecting sorting)
            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(reviewMapper.toResponseList(anyList())).thenAnswer(invocation -> {
                List<Review> reviews = invocation.getArgument(0);
                return reviews.stream()
                        .map(r -> new ReviewResponse(
                                r.getId(),
                                r.getRestaurantId(),
                                r.getUserId(),
                                r.getUserName(),
                                r.getContent(),
                                r.getRating(),
                                List.of(),
                                r.getCreatedAt(),
                                r.getLastEditedAt(),
                                true))
                        .toList();
            });

            // When
            var result = reviewService.getRestaurantReviews(testRestaurantId, "rating,asc", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(3, result.content().size());
            // Verify sorting by checking first review has lowest rating
            assertEquals(3, result.content().getFirst().rating());
            // Verify last review has highest rating
            assertEquals(5, result.content().get(2).rating());
        }

        @Test
        @DisplayName("shouldReturnEmptyPage when page number exceeds available pages")
        void shouldReturnEmptyPageWhenPageExceeds() {
            // Given
            Restaurant restaurant = createTestRestaurantWithReviews();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When
            var result = reviewService.getRestaurantReviews(testRestaurantId, "date,desc", 5, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(3, result.totalElements());
            assertEquals(1, result.totalPages());
            assertEquals(5, result.page());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when getting reviews for non-existent restaurant")
        void shouldThrowResourceNotFoundExceptionWhenGettingReviewsForNonExistentRestaurant() {
            // Given
            when(restaurantService.getRestaurantEntity(testRestaurantId))
                    .thenThrow(new ResourceNotFoundException("Restaurant not found with id " + testRestaurantId));

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> reviewService.getRestaurantReviews(testRestaurantId, null, 1, 10));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Update Review Tests")
    class UpdateReviewTests {

        @Test
        @DisplayName("shouldUpdateReviewSuccessfully when user is authorized and within edit window")
        void shouldUpdateReviewSuccessfully() {
            // Given
            Restaurant restaurant = createTestRestaurantWithExistingReview();
            var request = new UpdateReviewRequest("Updated content", 4, List.of("photo3"));

            var expectedResponse = new ReviewResponse(
                    testReviewId,
                    testRestaurantId,
                    testUserId,
                    testUserName,
                    "Updated content",
                    4,
                    List.of(),
                    Instant.now(),
                    Instant.now(),
                    true);

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(photoService.getPhotosByIds(request.photoIds())).thenReturn(List.of(createTestPhoto("photo3")));
            when(reviewMapper.toResponse(any(Review.class))).thenReturn(expectedResponse);

            // When
            ReviewResponse result = reviewService.updateReview(testRestaurantId, testReviewId, request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals("Updated content", result.content());
            assertEquals(4, result.rating());
            verify(restaurantService, times(1)).saveRestaurant(any(Restaurant.class));
            verify(photoService, times(1)).getPhotosByIds(request.photoIds());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when review does not exist")
        void shouldThrowResourceNotFoundExceptionWhenReviewDoesNotExist() {
            // Given
            Restaurant restaurant = createTestRestaurant();
            var request = new UpdateReviewRequest("Updated content", 4, null);

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> reviewService.updateReview(testRestaurantId, "non-existent-id", request, testUserId));

            assertEquals("Review not found with id non-existent-id", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not review owner")
        void shouldThrowUnauthorizedExceptionWhenUserNotReviewOwner() {
            // Given
            Restaurant restaurant = createTestRestaurantWithExistingReview();
            var request = new UpdateReviewRequest("Updated content", 4, null);

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> reviewService.updateReview(testRestaurantId, testReviewId, request, testOtherUserId));

            assertEquals("You are not authorized to update this review", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when review is outside edit window")
        void shouldThrowBusinessExceptionWhenReviewOutsideEditWindow() {
            // Given
            Restaurant restaurant = createTestRestaurantWithOldReview();
            var request = new UpdateReviewRequest("Updated content", 4, null);

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> reviewService.updateReview(testRestaurantId, testReviewId, request, testUserId));

            assertEquals("Reviews can only be edited within 48 hours of posting", exception.getMessage());
        }

        @Test
        @DisplayName("shouldUpdatePhotosWhenPhotoIdsAreEmpty")
        void shouldUpdatePhotosWhenPhotoIdsAreEmpty() {
            // Given
            Restaurant restaurant = createTestRestaurantWithExistingReview();
            var request = new UpdateReviewRequest("Updated content", 4, List.of());

            var expectedResponse = new ReviewResponse(
                    testReviewId,
                    testRestaurantId,
                    testUserId,
                    testUserName,
                    "Updated content",
                    4,
                    List.of(),
                    Instant.now(),
                    Instant.now(),
                    true);

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);
            when(reviewMapper.toResponse(any(Review.class))).thenReturn(expectedResponse);

            // When
            ReviewResponse result = reviewService.updateReview(testRestaurantId, testReviewId, request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(0, result.photos().size());
            verify(photoService, never()).getPhotosByIds(any());
        }
    }

    @Nested
    @DisplayName("Delete Review Tests")
    class DeleteReviewTests {

        @Test
        @DisplayName("shouldDeleteReviewSuccessfully when user is authorized")
        void shouldDeleteReviewSuccessfully() {
            // Given
            Restaurant restaurant = createTestRestaurantWithExistingReview();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When
            reviewService.deleteReview(testRestaurantId, testReviewId, testUserId);

            // Then
            verify(restaurantService, times(1)).saveRestaurant(any(Restaurant.class));
            assertEquals(0, restaurant.getReviews().size());
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when review does not exist for deletion")
        void shouldThrowResourceNotFoundExceptionWhenReviewDoesNotExistForDeletion() {
            // Given
            Restaurant restaurant = createTestRestaurant();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> reviewService.deleteReview(testRestaurantId, "non-existent-id", testUserId));

            assertEquals("Review not found with id non-existent-id", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not authorized to delete")
        void shouldThrowUnauthorizedExceptionWhenUserNotAuthorizedToDelete() {
            // Given
            Restaurant restaurant = createTestRestaurantWithExistingReview();

            when(restaurantService.getRestaurantEntity(testRestaurantId)).thenReturn(restaurant);

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> reviewService.deleteReview(testRestaurantId, testReviewId, testOtherUserId));

            assertEquals("You are not authorized to delete this review", exception.getMessage());
            verify(restaurantService, never()).saveRestaurant(any());
        }
    }

    // Helper methods
    private Restaurant createTestRestaurant() {
        return Restaurant.builder()
                .id(testRestaurantId)
                .name("Test Restaurant")
                .ownerId("owner-id")
                .averageRating(0.0)
                .totalReviews(0)
                .reviews(new ArrayList<>())
                .photos(new ArrayList<>())
                .build();
    }

    private Restaurant createTestRestaurantWithExistingReview() {
        Restaurant restaurant = createTestRestaurant();
        Review review = createTestReview();
        restaurant.getReviews().add(review);
        restaurant.setTotalReviews(1);
        return restaurant;
    }

    private Restaurant createTestRestaurantWithOldReview() {
        Restaurant restaurant = createTestRestaurant();
        Review review = createTestReview();
        // Set createdAt to 3 days ago (outside 48-hour window)
        review.setCreatedAt(Instant.now().minusSeconds(3 * 24 * 60 * 60));
        restaurant.getReviews().add(review);
        return restaurant;
    }

    private Restaurant createTestRestaurantWithReviews() {
        Restaurant restaurant = createTestRestaurant();
        var reviews = new ArrayList<Review>();

        Review review1 = Review.builder()
                .id("review1")
                .restaurantId(testRestaurantId)
                .userId(testUserId)
                .userName(testUserName)
                .content("Great food!")
                .rating(5)
                .photos(List.of(createTestPhoto("photo1")))
                .createdAt(Instant.now().minusSeconds(24 * 60 * 60)) // 1 day ago
                .lastEditedAt(Instant.now().minusSeconds(24 * 60 * 60))
                .build();

        Review review2 = Review.builder()
                .id("review2")
                .restaurantId(testRestaurantId)
                .userId("user2")
                .userName("User Two")
                .content("Good service")
                .rating(4)
                .photos(List.of(createTestPhoto("photo2")))
                .createdAt(Instant.now().minusSeconds(48 * 60 * 60)) // 2 days ago
                .lastEditedAt(Instant.now().minusSeconds(48 * 60 * 60))
                .build();

        Review review3 = Review.builder()
                .id("review3")
                .restaurantId(testRestaurantId)
                .userId("user3")
                .userName("User Three")
                .content("Average experience")
                .rating(3)
                .photos(new ArrayList<>())
                .createdAt(Instant.now().minusSeconds(72 * 60 * 60)) // 3 days ago
                .lastEditedAt(Instant.now().minusSeconds(72 * 60 * 60))
                .build();

        reviews.add(review1);
        reviews.add(review2);
        reviews.add(review3);

        restaurant.setReviews(reviews);
        restaurant.setTotalReviews(3);
        restaurant.setAverageRating(4.0);

        return restaurant;
    }

    private Review createTestReview() {
        return Review.builder()
                .id(testReviewId)
                .restaurantId(testRestaurantId)
                .userId(testUserId)
                .userName(testUserName)
                .content("Great food!")
                .rating(5)
                .photos(List.of(createTestPhoto("photo1"), createTestPhoto("photo2")))
                .createdAt(Instant.now())
                .lastEditedAt(Instant.now())
                .build();
    }

    private ReviewResponse createTestReviewResponse() {
        return new ReviewResponse(
                testReviewId,
                testRestaurantId,
                testUserId,
                testUserName,
                "Great food!",
                5,
                List.of(),
                Instant.now(),
                Instant.now(),
                true);
    }

    private Photo createTestPhoto(String photoId) {
        return Photo.builder()
                .id(photoId)
                .caption("Test photo")
                .uploadedAt(Instant.now())
                .uploadedBy(testUserId)
                .fileName(photoId + ".jpg")
                .contentType("image/jpeg")
                .filePath("/uploads/" + photoId + ".jpg")
                .build();
    }

    private List<Photo> createTestPhotos() {
        return List.of(createTestPhoto("photo1"), createTestPhoto("photo2"));
    }
}
