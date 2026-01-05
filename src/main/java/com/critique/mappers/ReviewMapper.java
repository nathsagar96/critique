package com.critique.mappers;

import com.critique.dtos.requests.CreateReviewRequest;
import com.critique.dtos.requests.UpdateReviewRequest;
import com.critique.dtos.responses.ReviewResponse;
import com.critique.entities.Review;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastEditedAt", ignore = true)
    Review toEntity(CreateReviewRequest request);

    @Mapping(target = "canEdit", expression = "java(review.canEdit(java.time.Instant.now()))")
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastEditedAt", ignore = true)
    void updateEntity(UpdateReviewRequest request, @MappingTarget Review review);
}
