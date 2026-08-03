package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForumService {

    @Autowired private ForumPostRepository postRepository;
    @Autowired private ForumCommentRepository commentRepository;
    @Autowired private ForumLikeRepository likeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PageResponse<ForumPost> getPosts(String category, String search, int page, int size) {
        ForumPost.ForumCategory cat = null;
        if (category != null && !category.isEmpty()) {
            try { cat = ForumPost.ForumCategory.valueOf(category.toUpperCase()); } catch (Exception ignored) {}
        }
        Page<ForumPost> result = postRepository.findPosts(cat, search, PageRequest.of(page, size));
        return PageResponse.<ForumPost>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Transactional
    public ForumPost createPost(Long authorId, ForumPostRequest req) {
        User author = userRepository.findById(authorId).orElseThrow();
        ForumPost post = ForumPost.builder()
                .author(author)
                .category(req.getCategory())
                .title(req.getTitle())
                .content(req.getContent())
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public ForumComment addComment(Long postId, Long authorId, String content, Long parentId) {
        ForumPost post = postRepository.findById(postId).orElseThrow();
        User author = userRepository.findById(authorId).orElseThrow();
        ForumComment comment = ForumComment.builder()
                .post(post)
                .author(author)
                .content(content)
                .build();
        if (parentId != null) {
            ForumComment parent = commentRepository.findById(parentId).orElse(null);
            comment.setParent(parent);
        }
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return commentRepository.save(comment);
    }

    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(likeRepository::delete);
            ForumPost post = postRepository.findById(postId).orElseThrow();
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            return false;
        } else {
            ForumPost post = postRepository.findById(postId).orElseThrow();
            User user = userRepository.findById(userId).orElseThrow();
            ForumLike like = ForumLike.builder().post(post).user(user).build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public long countHotPosts() {
        return postRepository.countByIsHotTrue();
    }
}
