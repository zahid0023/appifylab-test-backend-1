package com.example.appifylabtestbackend1.service;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.commons.dto.response.PaginatedResponse;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.dto.request.posts.CreatePostRequest;
import com.example.appifylabtestbackend1.dto.request.posts.UpdatePostRequest;
import com.example.appifylabtestbackend1.dto.response.PostResponse;
import com.example.appifylabtestbackend1.model.dto.PostDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import org.springframework.data.domain.Pageable;

public interface PostService {

    SuccessResponse createPost(CreatePostRequest request, UserEntity userEntity);

    PaginatedResponse<PostDto> getAllPosts(Pageable pageable);

    PaginatedResponse<PostDto> getMyPosts(UserEntity userEntity, Pageable pageable);

    PostEntity getPostEntityById(Long id);

    PostResponse getPostById(Long id);

    SuccessResponse updatePost(Long id, UpdatePostRequest request, UserEntity userEntity);

    SuccessResponse deletePost(Long id, UserEntity userEntity);
}
