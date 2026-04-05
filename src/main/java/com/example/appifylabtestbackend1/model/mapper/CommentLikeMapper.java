package com.example.appifylabtestbackend1.model.mapper;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.dto.CommentLikeDto;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.CommentLikeEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentLikeMapper {

    public static CommentLikeEntity create(CommentEntity commentEntity, UserEntity userEntity, Boolean isLike) {
        CommentLikeEntity entity = new CommentLikeEntity();
        entity.setCommentEntity(commentEntity);
        entity.setUserEntity(userEntity);
        entity.setIsLike(isLike);
        return entity;
    }

    public static CommentLikeDto toDto(CommentLikeEntity entity) {
        CommentLikeDto dto = new CommentLikeDto();
        dto.setId(entity.getId());
        dto.setCommentId(entity.getCommentEntity().getId());
        dto.setUserId(entity.getUserEntity().getId());
        dto.setIsLike(entity.getIsLike());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
