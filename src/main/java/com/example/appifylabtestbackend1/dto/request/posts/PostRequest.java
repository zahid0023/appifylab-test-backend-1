package com.example.appifylabtestbackend1.dto.request.posts;

import com.example.appifylabtestbackend1.dto.request.images.ImageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRequest {
    @NotBlank
    private String content;

    @NotNull
    private Boolean isPublic;
    @Valid
    private List<ImageRequest> images;
}
