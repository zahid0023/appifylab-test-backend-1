package com.example.appifylabtestbackend1.model.dto;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentLikeSummaryDto {
    private long likeCount;
    private long dislikeCount;
    private Boolean myReaction; // true = liked, false = disliked, null = no reaction
}
