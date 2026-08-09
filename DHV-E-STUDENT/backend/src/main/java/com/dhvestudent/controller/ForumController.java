package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(forumService.getPosts(category, search, page, size));
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestParam Long authorId, @Valid @RequestBody ForumPostRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Đăng bài thành công",
                forumService.createPost(authorId, req)));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                         @RequestParam Long authorId,
                                         @RequestParam String content,
                                         @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(forumService.addComment(postId, authorId, content, parentId)));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        boolean liked = forumService.toggleLike(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(liked ? "Đã thích" : "Đã bỏ thích", liked));
    }

    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(forumService.countHotPosts()));
    }
}
