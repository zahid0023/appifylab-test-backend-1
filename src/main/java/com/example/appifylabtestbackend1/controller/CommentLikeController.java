package com.example.appifylabtestbackend1.controller;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.auth.service.UserService;
import com.example.appifylabtestbackend1.commons.dto.request.PaginatedRequest;
import com.example.appifylabtestbackend1.dto.request.likes.LikeRequest;
import com.example.appifylabtestbackend1.service.CommentLikeService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/comments/{comment-id}/likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final UserService userService;

    public CommentLikeController(CommentLikeService commentLikeService, UserService userService) {
        this.commentLikeService = commentLikeService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> likeComment(@PathVariable("comment-id") Long commentId,
                                         @Valid @RequestBody LikeRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentLikeService.likeComment(commentId, request.getIsLike(), userEntity));
    }

    @GetMapping
    public ResponseEntity<?> getCommentReactions(@PathVariable("comment-id") Long commentId,
                                                 @Valid @ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "createdAt"));
        return ResponseEntity.ok(commentLikeService.getCommentReactions(commentId, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getCommentLikeSummary(@PathVariable("comment-id") Long commentId) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(commentLikeService.getCommentLikeSummary(commentId, userEntity));
    }

    @PutMapping
    public ResponseEntity<?> updateReaction(@PathVariable("comment-id") Long commentId,
                                            @Valid @RequestBody LikeRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(commentLikeService.updateReaction(commentId, request.getIsLike(), userEntity));
    }

    @DeleteMapping
    public ResponseEntity<?> removeReaction(@PathVariable("comment-id") Long commentId) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(commentLikeService.removeReaction(commentId, userEntity));
    }
}
