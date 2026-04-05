package com.example.appifylabtestbackend1.model.dto;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private long replyCount;
    private Instant createdAt;
    private Instant updatedAt;
}
