package com.example.appifylabtestbackend1.serviceImpl;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.response.CommentLikeSummaryResponse;
import com.example.appifylabtestbackend1.model.dto.CommentLikeDto;
import com.example.appifylabtestbackend1.model.dto.CommentLikeSummaryDto;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.CommentLikeEntity;
import com.example.appifylabtestbackend1.model.mapper.CommentLikeMapper;
import com.example.appifylabtestbackend1.repository.CommentLikeRepository;
import com.example.appifylabtestbackend1.service.CommentLikeService;
import com.example.appifylabtestbackend1.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentService commentService;

    public CommentLikeServiceImpl(CommentLikeRepository commentLikeRepository, CommentService commentService) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentService = commentService;
    }

    @Override
    @Transactional
    public SuccessResponse likeComment(Long commentId, Boolean isLike, UserEntity userEntity) {
        CommentEntity commentEntity = commentService.getCommentEntityById(commentId);

        commentLikeRepository.findByCommentEntityAndUserEntityAndIsDeleted(commentEntity, userEntity, false)
                .ifPresent(existing -> {
                    throw new IllegalStateException("You have already reacted to this comment");
                });

        CommentLikeEntity entity = CommentLikeMapper.create(commentEntity, userEntity, isLike);
        CommentLikeEntity saved = commentLikeRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse updateReaction(Long commentId, Boolean isLike, UserEntity userEntity) {
        CommentEntity commentEntity = commentService.getCommentEntityById(commentId);

        CommentLikeEntity entity = commentLikeRepository
                .findByCommentEntityAndUserEntityAndIsDeleted(commentEntity, userEntity, false)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found for comment id: " + commentId));

        entity.setIsLike(isLike);
        CommentLikeEntity saved = commentLikeRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse removeReaction(Long commentId, UserEntity userEntity) {
        CommentEntity commentEntity = commentService.getCommentEntityById(commentId);

        CommentLikeEntity entity = commentLikeRepository
                .findByCommentEntityAndUserEntityAndIsDeleted(commentEntity, userEntity, false)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found for comment id: " + commentId));

        commentLikeRepository.delete(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public PaginatedResponse<CommentLikeDto> getCommentReactions(Long commentId, Pageable pageable) {
        CommentEntity commentEntity = commentService.getCommentEntityById(commentId);
        Page<@NonNull CommentLikeDto> page = commentLikeRepository
                .findAllByCommentEntityAndIsActiveAndIsDeleted(commentEntity, true, false, pageable)
                .map(CommentLikeMapper::toDto);
        return new PaginatedResponse<>(page);
    }

    @Override
    public CommentLikeSummaryResponse getCommentLikeSummary(Long commentId, UserEntity userEntity) {
        CommentEntity commentEntity = commentService.getCommentEntityById(commentId);

        long likeCount = commentLikeRepository.countLikes(commentEntity, true, true, false);
        long dislikeCount = commentLikeRepository.countLikes(commentEntity, false, true, false);

        Boolean myReaction = commentLikeRepository
                .findByCommentEntityAndUserEntityAndIsDeleted(commentEntity, userEntity, false)
                .map(CommentLikeEntity::getIsLike)
                .orElse(null);

        CommentLikeSummaryDto dto = new CommentLikeSummaryDto();
        dto.setLikeCount(likeCount);
        dto.setDislikeCount(dislikeCount);
        dto.setMyReaction(myReaction);
        return new CommentLikeSummaryResponse(dto);
    }
}
