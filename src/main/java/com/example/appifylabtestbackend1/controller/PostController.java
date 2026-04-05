package com.example.appifylabtestbackend1.controller;

import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import com.example.appifylabtestbackend1.auth.service.UserService;
import com.example.appifylabtestbackend1.commons.dto.request.PaginatedRequest;
import com.example.appifylabtestbackend1.dto.request.posts.CreatePostRequest;
import com.example.appifylabtestbackend1.dto.request.posts.UpdatePostRequest;
import com.example.appifylabtestbackend1.service.PostService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, userEntity));
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@Valid @ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "createdAt", "updatedAt"));
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(@Valid @ParameterObject PaginatedRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        Pageable pageable = request.toPageable(Set.of("id", "createdAt", "updatedAt"));
        return ResponseEntity.ok(postService.getMyPosts(userEntity, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @Valid @RequestBody UpdatePostRequest request) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(postService.updatePost(id, request, userEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        UserEntity userEntity = userService.getAuthenticatedUserEntity();
        return ResponseEntity.ok(postService.deletePost(id, userEntity));
    }
}
