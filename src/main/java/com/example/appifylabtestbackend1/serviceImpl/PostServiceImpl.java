package com.example.appifylabtestbackend1.serviceImpl;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.request.posts.CreatePostRequest;
import com.example.appifylabtestbackend1.dto.request.posts.UpdatePostRequest;
import com.example.appifylabtestbackend1.dto.response.PostResponse;
import com.example.appifylabtestbackend1.model.dto.PostDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.mapper.PostMapper;
import com.example.appifylabtestbackend1.repository.PostRepository;
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
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public SuccessResponse createPost(CreatePostRequest request, UserEntity userEntity) {
        PostEntity post = PostMapper.fromRequest(request, userEntity);
        PostEntity saved = postRepository.save(post);
        return new SuccessResponse(true, saved.getId());
    }

    @Override
    public PaginatedResponse<PostDto> getAllPosts(Pageable pageable) {
        Page<@NonNull PostEntity> postEntityPage = postRepository.findAllByIsActiveAndIsDeleted(true, false, pageable);
        Page<@NonNull PostDto> postDtoPage = postEntityPage.map(PostMapper::toDto);
        return new PaginatedResponse<>(postDtoPage);
    }

    @Override
    public PaginatedResponse<PostDto> getMyPosts(UserEntity userEntity, Pageable pageable) {
        Page<@NonNull PostEntity> postEntityPage = postRepository.findAllByUserEntityAndIsActiveAndIsDeleted(userEntity, true, false, pageable);
        Page<@NonNull PostDto> postDtoPage = postEntityPage.map(PostMapper::toDto);
        return new PaginatedResponse<>(postDtoPage);
    }

    @Override
    public PostEntity getPostEntityById(Long id) {
        return postRepository.findByIdAndIsActiveAndIsDeleted(id, true, false)
                .orElseThrow(() -> new EntityNotFoundException("PostEntity with id: " + id + " not found"));
    }

    @Override
    @Transactional
    public PostResponse getPostById(Long id) {
        PostEntity postEntity = getPostEntityById(id);
        PostDto dto = PostMapper.toDto(postEntity);
        return new PostResponse(dto);
    }

    @Override
    @Transactional
    public SuccessResponse updatePost(Long id, UpdatePostRequest request, UserEntity userEntity) {
        PostEntity entity = postRepository.findByIdAndUserEntityAndIsActiveAndIsDeleted(id, userEntity, true, false)
                .orElseThrow(() -> new EntityNotFoundException("PostEntity with id: " + id + " not found"));
        PostMapper.update(entity, request);
        PostEntity saved = postRepository.save(entity);

        return new SuccessResponse(true, saved.getId());
    }

    @Override
    @Transactional
    public SuccessResponse deletePost(Long id, UserEntity userEntity) {
        PostEntity postEntity = postRepository.findByIdAndUserEntityAndIsActiveAndIsDeleted(id, userEntity, true, false)
                .orElseThrow(() -> new EntityNotFoundException("PostEntity with id: " + id + " not found"));
        postRepository.delete(postEntity);
        return new SuccessResponse(true, postEntity.getId());
    }
}
