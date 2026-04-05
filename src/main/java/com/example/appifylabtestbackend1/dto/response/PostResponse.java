package com.example.appifylabtestbackend1.dto.response;

import com.example.appifylabtestbackend1.model.dto.PostDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostResponse {
    private PostDto data;

    public PostResponse(PostDto data) {
        this.data = data;
    }
}
