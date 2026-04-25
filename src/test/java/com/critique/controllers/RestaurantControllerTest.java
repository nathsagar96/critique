package com.critique.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.critique.dtos.requests.AddressRequest;
import com.critique.dtos.requests.CreateRestaurantRequest;
import com.critique.dtos.requests.GeoLocationRequest;
import com.critique.dtos.requests.RestaurantSearchRequest;
import com.critique.dtos.requests.UpdateRestaurantRequest;
import com.critique.dtos.responses.AddressResponse;
import com.critique.dtos.responses.GeoLocationResponse;
import com.critique.dtos.responses.PageResponse;
import com.critique.dtos.responses.RestaurantResponse;
import com.critique.dtos.responses.RestaurantSummaryResponse;
import com.critique.services.RestaurantSearchService;
import com.critique.services.RestaurantService;
import com.critique.utils.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
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

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private RestaurantSearchService searchService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private final String testUserId = "test-user-id";
    private final String testRestaurantId = "test-restaurant-id";

    @Nested
    @DisplayName("Search Restaurants Tests")
    class SearchRestaurantsTests {

        @Test
        @DisplayName("shouldReturnPageResponse when search restaurants")
        void shouldReturnPageResponseWhenSearchRestaurants() throws Exception {
            // Given
            PageResponse<RestaurantSummaryResponse> mockResponse = getRestaurantSummaryResponsePageResponse();

            when(searchService.searchRestaurants(any(RestaurantSearchRequest.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("q", "Test")
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(testRestaurantId))
                    .andExpect(jsonPath("$.content[0].name").value("Test Restaurant"));
        }
    }

    private @NonNull PageResponse<RestaurantSummaryResponse> getRestaurantSummaryResponsePageResponse() {
        var addressResponse = new AddressResponse(
                "123",
                "Main St",
                "Apt 4B",
                "New York",
                "NY",
                "10001",
                "USA",
                new GeoLocationResponse(40.7128, -74.0060));

        return new PageResponse<>(
                List.of(new RestaurantSummaryResponse(
                        testRestaurantId, "Test Restaurant", "Italian", 4.5, 10, addressResponse)),
                1,
                1,
                1L,
                1);
    }

    @Nested
    @DisplayName("Create Restaurant Tests")
    class CreateRestaurantTests {

        @Test
        @DisplayName("shouldCreateRestaurantSuccessfully when valid request is provided")
        void shouldCreateRestaurantSuccessfully() throws Exception {
            // Given
            CreateRestaurantRequest request = getCreateRestaurantRequest();

            var expectedResponse = new RestaurantResponse(
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

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(restaurantService.createRestaurant(any(CreateRestaurantRequest.class), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testRestaurantId))
                    .andExpect(jsonPath("$.name").value("Test Restaurant"))
                    .andExpect(jsonPath("$.ownerId").value(testUserId));
        }
    }

    private static @NonNull CreateRestaurantRequest getCreateRestaurantRequest() {
        var addressRequest = new AddressRequest(
                "123", "Main St", null, "New York", "NY", "10001", "USA", new GeoLocationRequest(40.7128, -74.0060));

        return new CreateRestaurantRequest(
                "Test Restaurant",
                "Italian",
                "Delicious Italian food",
                "+1234567890",
                "https://test.com",
                addressRequest,
                null,
                List.of("photo1", "photo2"));
    }

    @Nested
    @DisplayName("Get Restaurant Tests")
    class GetRestaurantTests {

        @Test
        @DisplayName("shouldReturnRestaurant when restaurant exists")
        void shouldReturnRestaurantWhenExists() throws Exception {
            // Given
            var expectedResponse = new RestaurantResponse(
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

            when(restaurantService.getRestaurant(testRestaurantId)).thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{restaurantId}", testRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testRestaurantId))
                    .andExpect(jsonPath("$.name").value("Test Restaurant"));
        }
    }

    @Nested
    @DisplayName("Update Restaurant Tests")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("shouldUpdateRestaurantSuccessfully when user is authorized")
        void shouldUpdateRestaurantSuccessfullyWhenAuthorized() throws Exception {
            // Given
            var request = new UpdateRestaurantRequest(
                    "Updated Restaurant",
                    "French",
                    "Updated description",
                    "+9876543210",
                    "https://updated.com",
                    null,
                    null);

            var expectedResponse = new RestaurantResponse(
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

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(restaurantService.updateRestaurant(anyString(), any(UpdateRestaurantRequest.class), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{restaurantId}", testRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Restaurant"))
                    .andExpect(jsonPath("$.cuisineType").value("French"));
        }
    }

    @Nested
    @DisplayName("Delete Restaurant Tests")
    class DeleteRestaurantTests {

        @Test
        @DisplayName("shouldDeleteRestaurantSuccessfully when user is authorized")
        void shouldDeleteRestaurantSuccessfullyWhenAuthorized() throws Exception {
            // Given
            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/restaurants/{restaurantId}", testRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
