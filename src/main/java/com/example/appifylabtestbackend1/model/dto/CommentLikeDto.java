package com.example.appifylabtestbackend1.model.dto;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentLikeDto {
    private Long id;
    private Long commentId;
    private Long userId;
    private Boolean isLike;
    private Instant createdAt;
}
