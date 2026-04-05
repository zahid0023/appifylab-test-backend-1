package com.example.appifylabtestbackend1.dto.response;

import com.example.appifylabtestbackend1.model.dto.CommentLikeSummaryDto;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentLikeSummaryResponse {
    private CommentLikeSummaryDto data;

    public CommentLikeSummaryResponse(CommentLikeSummaryDto data) {
        this.data = data;
    }
}
