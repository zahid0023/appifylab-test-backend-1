package com.example.appifylabtestbackend1.dto.response;

import com.example.appifylabtestbackend1.model.dto.CommentDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentResponse {
    private CommentDto data;

    public CommentResponse(CommentDto data) {
        this.data = data;
    }
}
