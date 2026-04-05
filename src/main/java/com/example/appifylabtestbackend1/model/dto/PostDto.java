package com.example.appifylabtestbackend1.model.dto;


import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostDto {
    private Long id;
    private String content;
    private Boolean isPublic;
    private List<ImageDto> images;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
}
