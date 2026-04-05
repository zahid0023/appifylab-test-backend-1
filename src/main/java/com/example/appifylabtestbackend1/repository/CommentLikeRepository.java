package com.example.appifylabtestbackend1.repository;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.entity.CommentEntity;
import com.example.appifylabtestbackend1.model.entity.CommentLikeEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<@NonNull CommentLikeEntity, @NonNull Long> {

    Optional<@NonNull CommentLikeEntity> findByCommentEntityAndUserEntityAndIsDeleted(
            CommentEntity commentEntity, UserEntity userEntity, Boolean isDeleted);

    Page<@NonNull CommentLikeEntity> findAllByCommentEntityAndIsActiveAndIsDeleted(
            CommentEntity commentEntity, Boolean isActive, Boolean isDeleted, Pageable pageable);

    @Query("""
                select count(cl)
                from CommentLikeEntity cl
                where cl.commentEntity = :comment
                  and cl.isLike = :isLike
                  and cl.isActive = :isActive
                  and cl.isDeleted = :isDeleted
            """)
    long countLikes(
            @Param("comment") CommentEntity commentEntity,
            @Param("isLike") Boolean isLike,
            @Param("isActive") Boolean isActive,
            @Param("isDeleted") Boolean isDeleted
    );
}
