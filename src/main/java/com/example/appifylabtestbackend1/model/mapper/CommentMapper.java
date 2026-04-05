package com.example.appifylabtestbackend1.model.mapper;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.dto.CommentDto;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {

    public static CommentEntity create(String content, PostEntity postEntity,
                                       UserEntity userEntity, CommentEntity parentComment) {
        CommentEntity entity = new CommentEntity();
        entity.setContent(content);
        entity.setPostEntity(postEntity);
        entity.setUserEntity(userEntity);
        entity.setParentCommentEntity(parentComment);
        return entity;
    }

    public static CommentDto toDto(CommentEntity entity, long replyCount) {
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setPostId(entity.getPostEntity().getId());
        dto.setUserId(entity.getUserEntity().getId());
        dto.setParentCommentId(
                entity.getParentCommentEntity() != null ? entity.getParentCommentEntity().getId() : null);
        dto.setContent(entity.getContent());
        dto.setReplyCount(replyCount);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
