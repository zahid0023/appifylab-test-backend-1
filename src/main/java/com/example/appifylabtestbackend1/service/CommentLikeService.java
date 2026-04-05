package com.example.appifylabtestbackend1.service;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.response.CommentLikeSummaryResponse;
import com.example.appifylabtestbackend1.model.dto.CommentLikeDto;
import org.springframework.data.domain.Pageable;

public interface CommentLikeService {

    SuccessResponse likeComment(Long commentId, Boolean isLike, UserEntity userEntity);

    SuccessResponse updateReaction(Long commentId, Boolean isLike, UserEntity userEntity);

    SuccessResponse removeReaction(Long commentId, UserEntity userEntity);

    PaginatedResponse<CommentLikeDto> getCommentReactions(Long commentId, Pageable pageable);

    CommentLikeSummaryResponse getCommentLikeSummary(Long commentId, UserEntity userEntity);
}
