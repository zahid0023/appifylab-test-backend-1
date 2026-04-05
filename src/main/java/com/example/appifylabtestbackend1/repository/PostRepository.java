package com.example.appifylabtestbackend1.repository;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.model.entity.PostEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<@NonNull PostEntity, @NonNull Long> {
    Page<@NonNull PostEntity> findAllByIsActiveAndIsDeleted(Boolean active, Boolean isDeleted, Pageable pageable);

    Page<@NonNull PostEntity> findAllByUserEntityAndIsActiveAndIsDeleted(UserEntity userEntity, Boolean isActive, Boolean isDeleted, Pageable pageable);

    Optional<@NonNull PostEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean active, Boolean isDeleted);

    Optional<@NonNull PostEntity> findByIdAndUserEntityAndIsActiveAndIsDeleted(Long id, UserEntity userEntity, Boolean isActive, Boolean isDeleted);
}
