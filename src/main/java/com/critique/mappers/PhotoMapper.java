package com.critique.mappers;

import com.critique.dtos.requests.PhotoUploadRequest;
import com.critique.dtos.responses.PhotoResponse;
import com.critique.entities.Photo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    Photo toEntity(PhotoUploadRequest request);

    PhotoResponse toResponse(Photo photo);

    List<PhotoResponse> toResponseList(List<Photo> photos);
}
