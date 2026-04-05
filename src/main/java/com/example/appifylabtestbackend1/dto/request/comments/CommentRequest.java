package com.example.appifylabtestbackend1.dto.request.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentRequest {
    @NotBlank
    private String content;
}
