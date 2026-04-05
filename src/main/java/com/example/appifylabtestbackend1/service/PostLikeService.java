package com.example.appifylabtestbackend1.service;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.response.PostLikeSummaryResponse;
import com.example.appifylabtestbackend1.model.dto.PostLikeDto;
import com.example.appifylabtestbackend1.model.dto.PostLikeSummaryDto;
import org.springframework.data.domain.Pageable;

public interface PostLikeService {

    SuccessResponse likePost(Long postId, Boolean isLike, UserEntity userEntity);

    SuccessResponse updateReaction(Long postId, Boolean isLike, UserEntity userEntity);

    SuccessResponse removeReaction(Long postId, UserEntity userEntity);

    PaginatedResponse<PostLikeDto> getPostReactions(Long postId, Pageable pageable);

    PostLikeSummaryResponse getPostLikeSummary(Long postId, UserEntity userEntity);
}
