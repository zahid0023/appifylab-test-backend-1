package com.example.appifylabtestbackend1.model.dto;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageDto {
    private Long id;
    private String imageUrl;
    private String caption;
    private Boolean isDefault;
}
