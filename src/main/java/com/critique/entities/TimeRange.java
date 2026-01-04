package com.critique.entities;

import java.time.LocalTime;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {
    @Field(type = FieldType.Keyword)
    private LocalTime openTime;

    @Field(type = FieldType.Keyword)
    private LocalTime closeTime;

    @Field(type = FieldType.Boolean)
    private Boolean closed;
}
