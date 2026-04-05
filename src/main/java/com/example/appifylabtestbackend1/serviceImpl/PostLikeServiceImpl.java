package com.example.appifylabtestbackend1.serviceImpl;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.response.PostLikeSummaryResponse;
import com.example.appifylabtestbackend1.model.dto.PostLikeDto;
import com.example.appifylabtestbackend1.model.dto.PostLikeSummaryDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.entity.PostLikeEntity;
import com.example.appifylabtestbackend1.model.mapper.PostLikeMapper;
import com.example.appifylabtestbackend1.repository.PostLikeRepository;
import com.example.appifylabtestbackend1.service.PostLikeService;
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
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostService postService;

    public PostLikeServiceImpl(PostLikeRepository postLikeRepository, PostService postService) {
        this.postLikeRepository = postLikeRepository;
        this.postService = postService;
    }

    @Override
    @Transactional
    public SuccessResponse likePost(Long postId, Boolean isLike, UserEntity userEntity) {
        PostEntity postEntity = postService.getPostEntityById(postId);

        postLikeRepository.findByPostEntityAndUserEntityAndIsDeleted(postEntity, userEntity, false)
                .ifPresent(existing -> {
                    throw new IllegalStateException("You have already reacted to this post");
                });

        PostLikeEntity entity = PostLikeMapper.create(postEntity, userEntity, isLike);
        PostLikeEntity saved = postLikeRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse updateReaction(Long postId, Boolean isLike, UserEntity userEntity) {
        PostEntity postEntity = postService.getPostEntityById(postId);

        PostLikeEntity entity = postLikeRepository
                .findByPostEntityAndUserEntityAndIsDeleted(postEntity, userEntity, false)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found for post id: " + postId));

        entity.setIsLike(isLike);
        PostLikeEntity saved = postLikeRepository.save(entity);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse removeReaction(Long postId, UserEntity userEntity) {
        PostEntity postEntity = postService.getPostEntityById(postId);

        PostLikeEntity entity = postLikeRepository
                .findByPostEntityAndUserEntityAndIsDeleted(postEntity, userEntity, false)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found for post id: " + postId));

        postLikeRepository.delete(entity);
        return new SuccessResponse(true, entity.getId());
    }

    @Override
    public PaginatedResponse<PostLikeDto> getPostReactions(Long postId, Pageable pageable) {
        PostEntity postEntity = postService.getPostEntityById(postId);
        Page<@NonNull PostLikeDto> page = postLikeRepository
                .findAllByPostEntityAndIsActiveAndIsDeleted(postEntity, true, false, pageable)
                .map(PostLikeMapper::toDto);
        return new PaginatedResponse<>(page);
    }

    @Override
    public PostLikeSummaryResponse getPostLikeSummary(Long postId, UserEntity userEntity) {
        PostEntity postEntity = postService.getPostEntityById(postId);

        long likeCount = postLikeRepository
                .countLikes(postEntity, true, true, false);
        long dislikeCount = postLikeRepository
                .countLikes(postEntity, false, true, false);

        Boolean myReaction = postLikeRepository
                .findByPostEntityAndUserEntityAndIsDeleted(postEntity, userEntity, false)
                .map(PostLikeEntity::getIsLike)
                .orElse(null);

        PostLikeSummaryDto dto = new PostLikeSummaryDto();
        dto.setLikeCount(likeCount);
        dto.setDislikeCount(dislikeCount);
        dto.setMyReaction(myReaction);
        return new PostLikeSummaryResponse(dto);
    }
}
