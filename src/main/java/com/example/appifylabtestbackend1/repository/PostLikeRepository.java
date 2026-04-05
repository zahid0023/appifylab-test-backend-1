package com.example.appifylabtestbackend1.repository;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import com.example.appifylabtestbackend1.model.entity.PostLikeEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<@NonNull PostLikeEntity, @NonNull Long> {

    Optional<@NonNull PostLikeEntity> findByPostEntityAndUserEntityAndIsDeleted(
            PostEntity postEntity, UserEntity userEntity, Boolean isDeleted);

    Page<@NonNull PostLikeEntity> findAllByPostEntityAndIsActiveAndIsDeleted(
            PostEntity postEntity, Boolean isActive, Boolean isDeleted, Pageable pageable);

    @Query("""
                select count(pl)
                from PostLikeEntity pl
                where pl.postEntity = :post
                  and pl.isLike = :isLike
                  and pl.isActive = :isActive
                  and pl.isDeleted = :isDeleted
            """)
    long countLikes(
            @Param("post") PostEntity postEntity,
            @Param("isLike") Boolean isLike,
            @Param("isActive") Boolean isActive,
            @Param("isDeleted") Boolean isDeleted
    );
}
