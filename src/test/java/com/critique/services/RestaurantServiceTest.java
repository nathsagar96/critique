package com.critique.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private PhotoService photoService;

    @InjectMocks
    private RestaurantService restaurantService;

    private final String testUserId = "test-user-id";
    private final String testRestaurantId = "test-restaurant-id";

    @Nested
    @DisplayName("Create Restaurant Tests")
    class CreateRestaurantTests {

        @Test
        @DisplayName("shouldCreateRestaurantSuccessfully when valid request is provided")
        void shouldCreateRestaurantSuccessfully() {
            // Given
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    "Test Restaurant",
                    "Italian",
                    "Delicious Italian food",
                    "+1234567890",
                    "https://test.com",
                    null,
                    null,
                    List.of("photo1", "photo2"));

            Restaurant mockRestaurant = createTestRestaurant();
            RestaurantResponse expectedResponse = new RestaurantResponse(
                    testRestaurantId,
                    "Test Restaurant",
                    "Italian",
                    "Delicious Italian food",
                    "+1234567890",
                    "https://test.com",
                    testUserId,
                    0.0,
                    0,
                    null,
                    null,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now());

            List<Photo> mockPhotos = List.of(
                    Photo.builder().id("photo1").build(),
                    Photo.builder().id("photo2").build());

            when(photoService.getPhotosByIds(List.of("photo1", "photo2"))).thenReturn(mockPhotos);
            when(restaurantMapper.toEntity(any(CreateRestaurantRequest.class))).thenReturn(mockRestaurant);
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(mockRestaurant);
            when(restaurantMapper.toResponse(any(Restaurant.class))).thenReturn(expectedResponse);

            // When
            RestaurantResponse result = restaurantService.createRestaurant(request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertEquals(expectedResponse.name(), result.name());
            assertEquals(expectedResponse.ownerId(), result.ownerId());
            verify(restaurantRepository, times(1)).save(any(Restaurant.class));
            verify(restaurantMapper, times(1)).toResponse(any(Restaurant.class));
        }

        @Test
        @DisplayName("shouldCreateRestaurantSuccessfully when no photos are provided")
        void shouldCreateRestaurantSuccessfullyWhenNoPhotos() {
            // Given
            CreateRestaurantRequest request = new CreateRestaurantRequest(
                    "Test Restaurant",
                    "Italian",
                    "Delicious Italian food",
                    "+1234567890",
                    "https://test.com",
                    null,
                    null,
                    null);

            Restaurant mockRestaurant = createTestRestaurant();
            RestaurantResponse expectedResponse = new RestaurantResponse(
                    testRestaurantId,
                    "Test Restaurant",
                    "Italian",
                    "Delicious Italian food",
                    "+1234567890",
                    "https://test.com",
                    testUserId,
                    0.0,
                    0,
                    null,
                    null,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now());

            // No need to mock photoService.getPhotosByIds for null/empty list case
            when(restaurantMapper.toEntity(any(CreateRestaurantRequest.class))).thenReturn(mockRestaurant);
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(mockRestaurant);
            when(restaurantMapper.toResponse(any(Restaurant.class))).thenReturn(expectedResponse);

            // When
            RestaurantResponse result = restaurantService.createRestaurant(request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertTrue(result.photos().isEmpty());
            verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        }
    }

    @Nested
    @DisplayName("Get Restaurant Tests")
    class GetRestaurantTests {

        @Test
        @DisplayName("shouldReturnRestaurant when restaurant exists")
        void shouldReturnRestaurantWhenExists() {
            // Given
            Restaurant mockRestaurant = createTestRestaurant();
            RestaurantResponse expectedResponse = new RestaurantResponse(
                    testRestaurantId,
                    "Test Restaurant",
                    "Italian",
                    "Delicious Italian food",
                    "+1234567890",
                    "https://test.com",
                    testUserId,
                    4.5,
                    10,
                    null,
                    null,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now());

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(mockRestaurant));
            when(restaurantMapper.toResponse(mockRestaurant)).thenReturn(expectedResponse);

            // When
            RestaurantResponse result = restaurantService.getRestaurant(testRestaurantId);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertEquals(expectedResponse.name(), result.name());
            verify(restaurantRepository, times(1)).findById(testRestaurantId);
            verify(restaurantMapper, times(1)).toResponse(mockRestaurant);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when restaurant does not exist")
        void shouldThrowResourceNotFoundExceptionWhenNotExists() {
            // Given
            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class, () -> restaurantService.getRestaurant(testRestaurantId));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
            verify(restaurantRepository, times(1)).findById(testRestaurantId);
        }
    }

    @Nested
    @DisplayName("Update Restaurant Tests")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("shouldUpdateRestaurantSuccessfully when user is authorized")
        void shouldUpdateRestaurantSuccessfullyWhenAuthorized() {
            // Given
            Restaurant existingRestaurant = createTestRestaurant();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "Updated Restaurant",
                    "French",
                    "Updated description",
                    "+9876543210",
                    "https://updated.com",
                    null,
                    null);

            RestaurantResponse expectedResponse = new RestaurantResponse(
                    testRestaurantId,
                    "Updated Restaurant",
                    "French",
                    "Updated description",
                    "+9876543210",
                    "https://updated.com",
                    testUserId,
                    4.5,
                    10,
                    null,
                    null,
                    new ArrayList<>(),
                    Instant.now(),
                    Instant.now());

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(existingRestaurant));
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(existingRestaurant);
            when(restaurantMapper.toResponse(any(Restaurant.class))).thenReturn(expectedResponse);

            // When
            RestaurantResponse result = restaurantService.updateRestaurant(testRestaurantId, request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.name(), result.name());
            assertEquals(expectedResponse.cuisineType(), result.cuisineType());
            verify(restaurantRepository, times(1)).save(existingRestaurant);
            verify(restaurantMapper, times(1)).updateEntity(request, existingRestaurant);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when restaurant does not exist for update")
        void shouldThrowResourceNotFoundExceptionWhenNotExistsForUpdate() {
            // Given
            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "Updated Restaurant",
                    "French",
                    "Updated description",
                    "+9876543210",
                    "https://updated.com",
                    null,
                    null);

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.updateRestaurant(testRestaurantId, request, testUserId));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not authorized to update")
        void shouldThrowUnauthorizedExceptionWhenNotAuthorizedToUpdate() {
            // Given
            Restaurant existingRestaurant = createTestRestaurant();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                    "Updated Restaurant",
                    "French",
                    "Updated description",
                    "+9876543210",
                    "https://updated.com",
                    null,
                    null);

            String unauthorizedUserId = "unauthorized-user";

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(existingRestaurant));

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> restaurantService.updateRestaurant(testRestaurantId, request, unauthorizedUserId));

            assertEquals("You are not authorized to update this restaurant", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Restaurant Tests")
    class DeleteRestaurantTests {

        @Test
        @DisplayName("shouldDeleteRestaurantSuccessfully when user is authorized")
        void shouldDeleteRestaurantSuccessfullyWhenAuthorized() {
            // Given
            Restaurant existingRestaurant = createTestRestaurant();

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(existingRestaurant));

            // When
            restaurantService.deleteRestaurant(testRestaurantId, testUserId);

            // Then
            verify(restaurantRepository, times(1)).deleteById(testRestaurantId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when restaurant does not exist for deletion")
        void shouldThrowResourceNotFoundExceptionWhenNotExistsForDeletion() {
            // Given
            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> restaurantService.deleteRestaurant(testRestaurantId, testUserId));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not authorized to delete")
        void shouldThrowUnauthorizedExceptionWhenNotAuthorizedToDelete() {
            // Given
            Restaurant existingRestaurant = createTestRestaurant();
            String unauthorizedUserId = "unauthorized-user";

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(existingRestaurant));

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> restaurantService.deleteRestaurant(testRestaurantId, unauthorizedUserId));

            assertEquals("You are not authorized to delete this restaurant", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Supporting Methods Tests")
    class SupportingMethodsTests {

        @Test
        @DisplayName("shouldReturnRestaurantEntity when restaurant exists")
        void shouldReturnRestaurantEntityWhenExists() {
            // Given
            Restaurant mockRestaurant = createTestRestaurant();

            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.of(mockRestaurant));

            // When
            Restaurant result = restaurantService.getRestaurantEntity(testRestaurantId);

            // Then
            assertNotNull(result);
            assertEquals(mockRestaurant.getId(), result.getId());
            verify(restaurantRepository, times(1)).findById(testRestaurantId);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when restaurant entity does not exist")
        void shouldThrowResourceNotFoundExceptionWhenEntityNotExists() {
            // Given
            when(restaurantRepository.findById(testRestaurantId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class, () -> restaurantService.getRestaurantEntity(testRestaurantId));

            assertEquals("Restaurant not found with id " + testRestaurantId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldRecalculateAverageRating correctly when reviews exist")
        void shouldRecalculateAverageRatingCorrectly() {
            // Given
            Restaurant restaurant = createTestRestaurant();
            List<Review> reviews = List.of(
                    Review.builder().rating(4).build(),
                    Review.builder().rating(5).build(),
                    Review.builder().rating(3).build());
            restaurant.setReviews(reviews);

            // When
            restaurantService.recalculateAverageRating(restaurant);

            // Then
            assertEquals(4.0, restaurant.getAverageRating());
            assertEquals(3, restaurant.getTotalReviews());
        }

        @Test
        @DisplayName("shouldSetAverageRatingToZero when no reviews exist")
        void shouldSetAverageRatingToZeroWhenNoReviews() {
            // Given
            Restaurant restaurant = createTestRestaurant();
            restaurant.setReviews(new ArrayList<>());

            // When
            restaurantService.recalculateAverageRating(restaurant);

            // Then
            assertEquals(0.0, restaurant.getAverageRating());
            assertEquals(0, restaurant.getTotalReviews());
        }

        @Test
        @DisplayName("shouldSaveRestaurant successfully")
        void shouldSaveRestaurantSuccessfully() {
            // Given
            Restaurant restaurant = createTestRestaurant();

            // When
            restaurantService.saveRestaurant(restaurant);

            // Then
            verify(restaurantRepository, times(1)).save(restaurant);
        }
    }

    // Helper methods
    private Restaurant createTestRestaurant() {
        return Restaurant.builder()
                .id(testRestaurantId)
                .name("Test Restaurant")
                .cuisineType("Italian")
                .description("Delicious Italian food")
                .phoneNumber("+1234567890")
                .website("https://test.com")
                .ownerId(testUserId)
                .averageRating(4.5)
                .totalReviews(10)
                .photos(new ArrayList<>())
                .reviews(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
