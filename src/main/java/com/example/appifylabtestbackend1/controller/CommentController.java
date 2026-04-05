package com.example.appifylabtestbackend1.controller;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.auth.service.UserService;
import com.example.appifylabtestbackend1.commons.dto.request.PaginatedRequest;
import com.example.appifylabtestbackend1.dto.request.comments.CreateCommentRequest;
import com.example.appifylabtestbackend1.dto.request.comments.UpdateCommentRequest;
import com.example.appifylabtestbackend1.service.CommentService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/posts/{post-id}/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable("post-id") Long postId,
                                           @Valid @RequestBody CreateCommentRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, request, userEntity));
    }

    @GetMapping
    public ResponseEntity<?> getPostComments(@PathVariable("post-id") Long postId,
                                             @Valid @ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "createdAt", "updatedAt"));
        return ResponseEntity.ok(commentService.getPostComments(postId, pageable));
    }

    @GetMapping("/{comment-id}")
    public ResponseEntity<?> getCommentById(@PathVariable("post-id") Long ignoredPostId,
                                            @PathVariable("comment-id") Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping("/{comment-id}/replies")
    public ResponseEntity<?> getCommentReplies(@PathVariable("post-id") Long ignoredPostId,
                                               @PathVariable("comment-id") Long commentId,
                                               @Valid @ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "createdAt", "updatedAt"));
        return ResponseEntity.ok(commentService.getCommentReplies(commentId, pageable));
    }

    @PutMapping("/{comment-id}")
    public ResponseEntity<?> updateComment(@PathVariable("post-id") Long ignoredPostId,
                                           @PathVariable("comment-id") Long commentId,
                                           @Valid @RequestBody UpdateCommentRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(commentService.updateComment(commentId, request, userEntity));
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<?> deleteComment(@PathVariable("post-id") Long ignoredPostId,
                                           @PathVariable("comment-id") Long commentId) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(commentService.deleteComment(commentId, userEntity));
    }
}
