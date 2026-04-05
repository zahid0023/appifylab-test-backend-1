package com.example.appifylabtestbackend1.model.mapper;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.dto.PostLikeDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.entity.PostLikeEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostLikeMapper {

    public static PostLikeEntity create(PostEntity postEntity, UserEntity userEntity, Boolean isLike) {
        PostLikeEntity entity = new PostLikeEntity();
        entity.setPostEntity(postEntity);
        entity.setUserEntity(userEntity);
        entity.setIsLike(isLike);
        return entity;
    }

    public static PostLikeDto toDto(PostLikeEntity entity) {
        PostLikeDto dto = new PostLikeDto();
        dto.setId(entity.getId());
        dto.setPostId(entity.getPostEntity().getId());
        dto.setUserId(entity.getUserEntity().getId());
        dto.setIsLike(entity.getIsLike());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
