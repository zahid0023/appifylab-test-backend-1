package com.example.appifylabtestbackend1.controller;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.auth.service.UserService;
import com.example.appifylabtestbackend1.commons.dto.request.PaginatedRequest;
import com.example.appifylabtestbackend1.dto.request.likes.LikeRequest;
import com.example.appifylabtestbackend1.service.PostLikeService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/posts/{post-id}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final UserService userService;

    public PostLikeController(PostLikeService postLikeService, UserService userService) {
        this.postLikeService = postLikeService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> likePost(@PathVariable("post-id") Long postId,
                                      @Valid @RequestBody LikeRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postLikeService.likePost(postId, request.getIsLike(), userEntity));
    }

    @GetMapping
    public ResponseEntity<?> getPostReactions(@PathVariable("post-id") Long postId,
                                              @Valid @ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "createdAt"));
        return ResponseEntity.ok(postLikeService.getPostReactions(postId, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getPostLikeSummary(@PathVariable("post-id") Long postId) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(postLikeService.getPostLikeSummary(postId, userEntity));
    }

    @PutMapping
    public ResponseEntity<?> updateReaction(@PathVariable("post-id") Long postId,
                                            @Valid @RequestBody LikeRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(postLikeService.updateReaction(postId, request.getIsLike(), userEntity));
    }

    @DeleteMapping
    public ResponseEntity<?> removeReaction(@PathVariable("post-id") Long postId) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(postLikeService.removeReaction(postId, userEntity));
    }
}
