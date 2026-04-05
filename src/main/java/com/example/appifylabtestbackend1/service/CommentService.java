package com.example.appifylabtestbackend1.service;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.request.comments.CreateCommentRequest;
import com.example.appifylabtestbackend1.dto.request.comments.UpdateCommentRequest;
import com.example.appifylabtestbackend1.dto.response.CommentResponse;
import com.example.appifylabtestbackend1.model.dto.CommentDto;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    SuccessResponse createComment(Long postId, CreateCommentRequest request, UserEntity userEntity);

    PaginatedResponse<CommentDto> getPostComments(Long postId, Pageable pageable);

    PaginatedResponse<CommentDto> getCommentReplies(Long commentId, Pageable pageable);

    CommentResponse getCommentById(Long commentId);

    SuccessResponse updateComment(Long commentId, UpdateCommentRequest request, UserEntity userEntity);

    SuccessResponse deleteComment(Long commentId, UserEntity userEntity);

    CommentEntity getCommentEntityById(Long commentId);
}
