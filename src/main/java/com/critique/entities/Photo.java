package com.critique.entities;

import java.time.Instant;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Text)
    private String caption;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant uploadedAt;

    @Field(type = FieldType.Keyword)
    private String uploadedBy;
}
