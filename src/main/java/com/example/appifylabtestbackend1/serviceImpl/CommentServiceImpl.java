package com.example.appifylabtestbackend1.serviceImpl;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.request.comments.CreateCommentRequest;
import com.example.appifylabtestbackend1.dto.request.comments.UpdateCommentRequest;
import com.example.appifylabtestbackend1.dto.response.CommentResponse;
import com.example.appifylabtestbackend1.model.dto.CommentDto;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.mapper.CommentMapper;
import com.example.appifylabtestbackend1.repository.CommentRepository;
import com.example.appifylabtestbackend1.service.CommentService;
import com.example.appifylabtestbackend1.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Override
    @Transactional
    public SuccessResponse createComment(Long postId, CreateCommentRequest request, UserEntity userEntity) {
        PostEntity postEntity = postService.getPostEntityById(postId);

        CommentEntity parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository
                    .findByIdAndIsActiveAndIsDeleted(request.getParentCommentId(), true, false)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent comment not found with id: " + request.getParentCommentId()));

            if (!parentComment.getPostEntity().getId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to the specified post");
            }
        }

        CommentEntity entity = CommentMapper.create(request.getContent(), postEntity, userEntity, parentComment);
        CommentEntity saved = commentRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public PaginatedResponse<CommentDto> getPostComments(Long postId, Pageable pageable) {
        PostEntity postEntity = postService.getPostEntityById(postId);
        Page<@NonNull CommentDto> page = commentRepository
                .findAllByPostEntityAndParentCommentEntityIsNullAndIsActiveAndIsDeleted(
                        postEntity, true, false, pageable)
                .map(this::toDtoWithReplyCount);
        return new PaginatedResponse<>(page);
    }

    @Override
    @Transactional
    public PaginatedResponse<CommentDto> getCommentReplies(Long commentId, Pageable pageable) {
        CommentEntity parentComment = getCommentEntityById(commentId);
        Page<@NonNull CommentDto> page = commentRepository
                .findAllByParentCommentEntityAndIsActiveAndIsDeleted(parentComment, true, false, pageable)
                .map(this::toDtoWithReplyCount);
        return new PaginatedResponse<>(page);
    }

    @Override
    @Transactional
    public CommentResponse getCommentById(Long commentId) {
        CommentEntity entity = getCommentEntityById(commentId);
        return new CommentResponse(toDtoWithReplyCount(entity));
    }

    @Override
    @Transactional
    public SuccessResponse updateComment(Long commentId, UpdateCommentRequest request, UserEntity userEntity) {
        CommentEntity entity = commentRepository
                .findByIdAndUserEntityAndIsActiveAndIsDeleted(commentId, userEntity, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        entity.setContent(request.getContent());
        CommentEntity saved = commentRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse deleteComment(Long commentId, UserEntity userEntity) {
        CommentEntity entity = commentRepository
                .findByIdAndUserEntityAndIsActiveAndIsDeleted(commentId, userEntity, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        commentRepository.delete(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public CommentEntity getCommentEntityById(Long commentId) {
        return commentRepository.findByIdAndIsActiveAndIsDeleted(commentId, true, false)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
    }

    private CommentDto toDtoWithReplyCount(CommentEntity entity) {
        long replyCount = commentRepository.countByParentCommentEntityAndIsActiveAndIsDeleted(entity, true, false);
        return CommentMapper.toDto(entity, replyCount);
    }
}
