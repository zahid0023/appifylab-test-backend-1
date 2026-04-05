package com.example.appifylabtestbackend1.dto.request.images;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageRequest {
    @NotBlank
    private String imageUrl;
    private String caption;

    @NotNull
    private Boolean isDefault;
}
