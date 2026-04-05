package com.example.appifylabtestbackend1.model.mapper;

import com.example.appifylabtestbackend1.dto.request.images.ImageRequest;
import com.example.appifylabtestbackend1.model.dto.ImageDto;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.entity.PostImageEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostImageMapper {
    public static PostImageEntity fromRequest(ImageRequest request, PostEntity postEntity) {
        PostImageEntity entity = new PostImageEntity();
        entity.setPostEntity(postEntity);
        entity.setImageUrl(request.getImageUrl());
        entity.setCaption(request.getCaption());
        entity.setIsDefault(request.getIsDefault());
        return entity;
    }

    public static ImageDto toDto(PostImageEntity entity) {
        ImageDto dto = new ImageDto();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCaption(entity.getCaption());
        dto.setIsDefault(entity.getIsDefault());
        return dto;
    }
}
