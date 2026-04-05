package com.example.appifylabtestbackend1.model.mapper;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.dto.request.posts.CreatePostRequest;
import com.example.appifylabtestbackend1.dto.request.posts.UpdatePostRequest;
import com.example.appifylabtestbackend1.model.dto.PostDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.entity.PostImageEntity;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class PostMapper {

    public static PostEntity fromRequest(CreatePostRequest request, UserEntity user) {
        PostEntity entity = new PostEntity();
        entity.setContent(request.getContent());
        entity.setIsPublic(request.getIsPublic());
        entity.setUserEntity(user);
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            Set<PostImageEntity> postImageEntities = request.getImages().stream()
                    .map(imageRequest -> PostImageMapper.fromRequest(imageRequest, entity))
                    .collect(Collectors.toSet());
            entity.setPostImageEntities(postImageEntities);
        }
        return entity;
    }

    public static PostEntity update(PostEntity entity, UpdatePostRequest request) {
        entity.setContent(request.getContent());
        entity.setIsPublic(request.getIsPublic());
        if (request.getImages() != null) {
            entity.getPostImageEntities().clear();
            request.getImages().stream()
                    .map(imageRequest -> PostImageMapper.fromRequest(imageRequest, entity))
                    .forEach(entity.getPostImageEntities()::add);
        }
        return entity;
    }

    public static PostDto toDto(PostEntity entity) {
        PostDto dto = new PostDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserEntity().getId());
        dto.setContent(entity.getContent());
        dto.setIsPublic(entity.getIsPublic());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setImages(entity.getPostImageEntities().stream()
                .map(PostImageMapper::toDto)
                .toList());
        return dto;
    }
}
