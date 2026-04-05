package com.example.appifylabtestbackend1.dto.request.likes;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LikeRequest {

    @NotNull
    private Boolean isLike;
}
