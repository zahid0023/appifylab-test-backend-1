package com.example.appifylabtestbackend1.repository;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<@NonNull CommentEntity, @NonNull Long> {

    Page<@NonNull CommentEntity> findAllByPostEntityAndParentCommentEntityIsNullAndIsActiveAndIsDeleted(
            PostEntity postEntity, Boolean isActive, Boolean isDeleted, Pageable pageable);

    Page<@NonNull CommentEntity> findAllByParentCommentEntityAndIsActiveAndIsDeleted(
            CommentEntity parentComment, Boolean isActive, Boolean isDeleted, Pageable pageable);

    Optional<@NonNull CommentEntity> findByIdAndIsActiveAndIsDeleted(
            Long id, Boolean isActive, Boolean isDeleted);

    Optional<@NonNull CommentEntity> findByIdAndUserEntityAndIsActiveAndIsDeleted(
            Long id, UserEntity userEntity, Boolean isActive, Boolean isDeleted);

    long countByParentCommentEntityAndIsActiveAndIsDeleted(
            CommentEntity parentComment, Boolean isActive, Boolean isDeleted);
}
