package com.critique.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.entities.Photo;
import com.critique.exceptions.BusinessException;
import com.critique.exceptions.UnauthorizedException;
import com.critique.mappers.PhotoMapper;
import com.critique.repositories.PhotoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private PhotoService photoService;

    private final String testUserId = "test-user-id";
    private final String testPhotoId = "test-photo-id";
    private String uploadDir;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        uploadDir = tempDir.toString();
        ReflectionTestUtils.setField(photoService, "uploadDir", uploadDir);
        ReflectionTestUtils.setField(photoService, "maxFileSize", 5242880L); // 5MB
    }

    @Nested
    @DisplayName("Photo Upload Tests")
    class PhotoUploadTests {

        @Test
        @DisplayName("shouldUploadPhotoSuccessfully when valid file and request are provided")
        void shouldUploadPhotoSuccessfully() {
            // Given
            MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());

            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");
            Photo mockPhoto = createTestPhoto();
            PhotoResponse expectedResponse = new PhotoResponse(testPhotoId, "Test caption", Instant.now(), testUserId);

            when(photoRepository.save(any(Photo.class))).thenReturn(mockPhoto);
            when(photoMapper.toResponse(any(Photo.class))).thenReturn(expectedResponse);

            // When
            PhotoResponse result = photoService.uploadPhoto(file, request, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertEquals(expectedResponse.caption(), result.caption());
            assertEquals(expectedResponse.uploadedBy(), result.uploadedBy());
            verify(photoRepository, times(1)).save(any(Photo.class));
            verify(photoMapper, times(1)).toResponse(any(Photo.class));
        }

        @Test
        @DisplayName("shouldUploadPhotoSuccessfully when file is uploaded without caption")
        void shouldUploadPhotoSuccessfullyWithoutCaption() {
            // Given
            MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());

            PhotoUploadRequest request = new PhotoUploadRequest(null);
            Photo mockPhoto = createTestPhoto();
            PhotoResponse expectedResponse = new PhotoResponse(testPhotoId, null, Instant.now(), testUserId);

            when(photoRepository.save(any(Photo.class))).thenReturn(mockPhoto);
            when(photoMapper.toResponse(any(Photo.class))).thenReturn(expectedResponse);

            // When
            PhotoResponse result = photoService.uploadPhoto(file, request, testUserId);

            // Then
            assertNotNull(result);
            assertNull(result.caption());
            verify(photoRepository, times(1)).save(any(Photo.class));
        }

        @Test
        @DisplayName("shouldThrowBusinessException when file is null")
        void shouldThrowBusinessExceptionWhenFileIsNull() {
            // Given
            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.uploadPhoto(null, request, testUserId));

            assertEquals("File is required", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when file is empty")
        void shouldThrowBusinessExceptionWhenFileIsEmpty() {
            // Given
            MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.uploadPhoto(file, request, testUserId));

            assertEquals("File is required", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when file size exceeds maximum allowed")
        void shouldThrowBusinessExceptionWhenFileSizeExceedsMaximum() {
            // Given
            byte[] largeContent = new byte[5242881]; // 5MB + 1 byte
            MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeContent);

            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.uploadPhoto(file, request, testUserId));

            assertEquals("File size exceeds maximum allowed size of 5242880 bytes", exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when file type is not allowed")
        void shouldThrowBusinessExceptionWhenFileTypeNotAllowed() {
            // Given
            MockMultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", "test content".getBytes());

            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.uploadPhoto(file, request, testUserId));

            assertTrue(exception.getMessage().contains("Invalid file type"));
            assertTrue(exception.getMessage().contains("image/jpeg"));
            assertTrue(exception.getMessage().contains("image/jpg"));
            assertTrue(exception.getMessage().contains("image/png"));
            assertTrue(exception.getMessage().contains("image/webp"));
        }

        @Test
        @DisplayName("shouldThrowBusinessException when file has no name")
        void shouldThrowBusinessExceptionWhenFileHasNoName() {
            // Given
            MockMultipartFile file = new MockMultipartFile("file", "", "image/jpeg", "test content".getBytes());

            PhotoUploadRequest request = new PhotoUploadRequest("Test caption");

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.uploadPhoto(file, request, testUserId));

            assertEquals("File must have a valid name", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Photos Tests")
    class GetPhotosTests {

        @Test
        @DisplayName("shouldReturnEmptyList when photoIds is null")
        void shouldReturnEmptyListWhenPhotoIdsIsNull() {
            // When
            List<Photo> result = photoService.getPhotosByIds(null);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("shouldReturnEmptyList when photoIds is empty")
        void shouldReturnEmptyListWhenPhotoIdsIsEmpty() {
            // When
            List<Photo> result = photoService.getPhotosByIds(List.of());

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("shouldReturnPhotos when valid photoIds are provided")
        void shouldReturnPhotosWhenValidPhotoIdsAreProvided() {
            // Given
            List<String> photoIds = List.of("photo1", "photo2");
            Photo photo1 = createTestPhotoWithId("photo1");
            Photo photo2 = createTestPhotoWithId("photo2");

            when(photoRepository.findAllById(photoIds)).thenReturn(List.of(photo1, photo2));

            // When
            List<Photo> result = photoService.getPhotosByIds(photoIds);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(photo1));
            assertTrue(result.contains(photo2));
        }
    }

    @Nested
    @DisplayName("Get Photo Tests")
    class GetPhotoTests {

        @Test
        @DisplayName("shouldReturnPhotoBytes when photo exists")
        void shouldReturnPhotoBytesWhenPhotoExists(@TempDir Path tempDir) throws IOException {
            // Given
            Photo mockPhoto = createTestPhoto();
            Path testFile = tempDir.resolve("test-photo.jpg");
            Files.write(testFile, "test content".getBytes());

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(mockPhoto));
            ReflectionTestUtils.setField(mockPhoto, "filePath", testFile.toString());

            // When
            byte[] result = photoService.getPhoto(testPhotoId);

            // Then
            assertNotNull(result);
            assertEquals("test content", new String(result));
        }

        @Test
        @DisplayName("shouldThrowBusinessException when photo does not exist")
        void shouldThrowBusinessExceptionWhenPhotoDoesNotExist() {
            // Given
            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.getPhoto(testPhotoId));

            assertEquals("Photo not found with ID: " + testPhotoId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowBusinessException when photo file cannot be read")
        void shouldThrowBusinessExceptionWhenPhotoFileCannotBeRead(@TempDir Path tempDir) {
            // Given
            Photo mockPhoto = createTestPhoto();
            Path nonExistentFile = tempDir.resolve("non-existent.jpg");

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(mockPhoto));
            ReflectionTestUtils.setField(mockPhoto, "filePath", nonExistentFile.toString());

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.getPhoto(testPhotoId));

            assertTrue(exception.getMessage().contains("Failed to read photo file"));
        }
    }

    @Nested
    @DisplayName("Get Photo Content Type Tests")
    class GetPhotoContentTypeTests {

        @Test
        @DisplayName("shouldReturnContentType when photo exists")
        void shouldReturnContentTypeWhenPhotoExists() {
            // Given
            Photo mockPhoto = createTestPhoto();
            String expectedContentType = "image/jpeg";

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(mockPhoto));
            ReflectionTestUtils.setField(mockPhoto, "contentType", expectedContentType);

            // When
            String result = photoService.getPhotoContentType(testPhotoId);

            // Then
            assertEquals(expectedContentType, result);
        }

        @Test
        @DisplayName("shouldThrowBusinessException when photo does not exist for content type")
        void shouldThrowBusinessExceptionWhenPhotoDoesNotExistForContentType() {
            // Given
            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception =
                    assertThrows(BusinessException.class, () -> photoService.getPhotoContentType(testPhotoId));

            assertEquals("Photo not found with ID: " + testPhotoId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Update Caption Tests")
    class UpdateCaptionTests {

        @Test
        @DisplayName("shouldUpdateCaptionSuccessfully when user is authorized")
        void shouldUpdateCaptionSuccessfullyWhenUserIsAuthorized() {
            // Given
            Photo existingPhoto = createTestPhoto();
            String newCaption = "Updated caption";
            PhotoResponse expectedResponse = new PhotoResponse(testPhotoId, newCaption, Instant.now(), testUserId);

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(existingPhoto));
            when(photoRepository.save(any(Photo.class))).thenReturn(existingPhoto);
            when(photoMapper.toResponse(any(Photo.class))).thenReturn(expectedResponse);

            // When
            PhotoResponse result = photoService.updateCaption(testPhotoId, newCaption, testUserId);

            // Then
            assertNotNull(result);
            assertEquals(newCaption, result.caption());
            verify(photoRepository, times(1)).save(existingPhoto);
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when photo does not exist for update")
        void shouldThrowResourceNotFoundExceptionWhenPhotoDoesNotExistForUpdate() {
            // Given
            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> photoService.updateCaption(testPhotoId, "New caption", testUserId));

            assertEquals("Photo not found with ID: " + testPhotoId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not authorized to update")
        void shouldThrowUnauthorizedExceptionWhenUserIsNotAuthorizedToUpdate() {
            // Given
            Photo existingPhoto = createTestPhoto();
            String unauthorizedUserId = "unauthorized-user";

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(existingPhoto));

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> photoService.updateCaption(testPhotoId, "New caption", unauthorizedUserId));

            assertEquals("You are not authorized to update this photo", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Photo Tests")
    class DeletePhotoTests {

        @Test
        @DisplayName("shouldDeletePhotoSuccessfully when user is authorized")
        void shouldDeletePhotoSuccessfullyWhenUserIsAuthorized(@TempDir Path tempDir) throws IOException {
            // Given
            Photo existingPhoto = createTestPhoto();
            Path testFile = tempDir.resolve("test-photo.jpg");
            Files.write(testFile, "test content".getBytes());

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(existingPhoto));
            ReflectionTestUtils.setField(existingPhoto, "filePath", testFile.toString());

            // When
            photoService.deletePhoto(testPhotoId, testUserId);

            // Then
            verify(photoRepository, times(1)).delete(existingPhoto);
            assertFalse(Files.exists(testFile));
        }

        @Test
        @DisplayName("shouldThrowResourceNotFoundException when photo does not exist for deletion")
        void shouldThrowResourceNotFoundExceptionWhenPhotoDoesNotExistForDeletion() {
            // Given
            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class, () -> photoService.deletePhoto(testPhotoId, testUserId));

            assertEquals("Photo not found with ID: " + testPhotoId, exception.getMessage());
        }

        @Test
        @DisplayName("shouldThrowUnauthorizedException when user is not authorized to delete")
        void shouldThrowUnauthorizedExceptionWhenUserIsNotAuthorizedToDelete() {
            // Given
            Photo existingPhoto = createTestPhoto();
            String unauthorizedUserId = "unauthorized-user";

            when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(existingPhoto));

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class, () -> photoService.deletePhoto(testPhotoId, unauthorizedUserId));

            assertEquals("You are not authorized to delete this photo", exception.getMessage());
        }
    }

    // Helper methods
    private Photo createTestPhoto() {
        return Photo.builder()
                .id(testPhotoId)
                .caption("Test caption")
                .uploadedAt(Instant.now())
                .uploadedBy(testUserId)
                .fileName("test-photo.jpg")
                .contentType("image/jpeg")
                .filePath(uploadDir + "/test-photo.jpg")
                .build();
    }

    private Photo createTestPhotoWithId(String photoId) {
        return Photo.builder()
                .id(photoId)
                .caption("Test caption")
                .uploadedAt(Instant.now())
                .uploadedBy(testUserId)
                .fileName(photoId + ".jpg")
                .contentType("image/jpeg")
                .filePath(uploadDir + "/" + photoId + ".jpg")
                .build();
    }
}
