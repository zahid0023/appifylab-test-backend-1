package com.example.appifylabtestbackend1.dto.response;

import com.example.appifylabtestbackend1.model.dto.PostLikeSummaryDto;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostLikeSummaryResponse {
    private PostLikeSummaryDto data;

    public PostLikeSummaryResponse(PostLikeSummaryDto data) {
        this.data = data;
    }
}
