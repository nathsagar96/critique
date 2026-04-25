package com.critique.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.services.PhotoService;
import com.critique.utils.SecurityUtils;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PhotoService photoService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private final String testUserId = "test-user-id";
    private final String testPhotoId = "test-photo-id";
    private final byte[] testPhotoData = "test photo data".getBytes();

    @Nested
    @DisplayName("Upload Photo Tests")
    class UploadPhotoTests {

        @Test
        @DisplayName("shouldUploadPhotoSuccessfully when valid file and caption are provided")
        void shouldUploadPhotoSuccessfully() throws Exception {
            // Given
            var file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, testPhotoData);

            var testCaption = "Test caption";
            var request = new PhotoUploadRequest(testCaption);

            var expectedResponse = new PhotoResponse(testPhotoId, testCaption, Instant.now(), testUserId);

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(photoService.uploadPhoto(any(MockMultipartFile.class), any(PhotoUploadRequest.class), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(multipart("/api/v1/photos")
                            .file(file)
                            .file(
                                    "caption",
                                    objectMapper.writeValueAsString(request).getBytes())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testPhotoId))
                    .andExpect(jsonPath("$.caption").value(testCaption))
                    .andExpect(jsonPath("$.uploadedBy").value(testUserId));
        }
    }

    @Nested
    @DisplayName("Get Photo Tests")
    class GetPhotoTests {

        @Test
        @DisplayName("shouldReturnPhotoData when photo exists")
        void shouldReturnPhotoDataWhenPhotoExists() throws Exception {
            // Given
            when(photoService.getPhoto(anyString())).thenReturn(testPhotoData);
            var testContentType = "image/jpeg";
            when(photoService.getPhotoContentType(anyString())).thenReturn(testContentType);

            // When & Then
            mockMvc.perform(get("/api/v1/photos/{photoId}", testPhotoId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(testContentType))
                    .andExpect(content().bytes(testPhotoData));
        }
    }

    @Nested
    @DisplayName("Update Caption Tests")
    class UpdateCaptionTests {

        @Test
        @DisplayName("shouldUpdateCaptionSuccessfully when user is authorized")
        void shouldUpdateCaptionSuccessfullyWhenAuthorized() throws Exception {
            // Given
            var request = new PhotoUploadRequest("Updated caption");

            var expectedResponse = new PhotoResponse(testPhotoId, "Updated caption", Instant.now(), testUserId);

            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);
            when(photoService.updateCaption(anyString(), anyString(), anyString()))
                    .thenReturn(expectedResponse);

            // When & Then
            mockMvc.perform(patch("/api/v1/photos/{photoId}", testPhotoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.caption").value("Updated caption"));
        }
    }

    @Nested
    @DisplayName("Delete Photo Tests")
    class DeletePhotoTests {

        @Test
        @DisplayName("shouldDeletePhotoSuccessfully when user is authorized")
        void shouldDeletePhotoSuccessfullyWhenAuthorized() throws Exception {
            // Given
            when(securityUtils.getCurrentUserId()).thenReturn(testUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/photos/{photoId}", testPhotoId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
