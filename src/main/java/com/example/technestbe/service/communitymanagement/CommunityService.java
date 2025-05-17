package com.example.technestbe.service.communitymanagement;

import com.example.technestbe.config.usermanagement.CustomUserDetails;
import com.example.technestbe.dto.communitymanagement.*;
import com.example.technestbe.model.communitymanagement.CommunityComment;
import com.example.technestbe.model.communitymanagement.CommunityPost;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.communitymanagement.CommunityCommentRepository;
import com.example.technestbe.repo.communitymanagement.CommunityPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private CommunityCommentRepository commentRepository;

    // This method is used to create a new post
    @Transactional
    public CommunityPostResponseDTO createPost(CommunityPostCreateDTO postCreateDTO) {
        User user = getCurrentUser();
        CommunityPost post = new CommunityPost();
        post.setTitle(postCreateDTO.getTitle());
        post.setDescription(postCreateDTO.getDescription());
        post.setUser(user);

        post = postRepository.save(post);
        return mapToPostResponseDTO(post);
    }

    // This method is used to get all posts
    public Page<CommunityPostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::mapToPostResponseDTO);
    }

    // This method is used to get posts by user
    public Page<CommunityPostResponseDTO> getUserPosts(Pageable pageable) {
        User user = getCurrentUser();
        return postRepository.findByUserId(user.getId(), pageable).map(this::mapToPostResponseDTO);
    }

    // This method is used to update a post
    @Transactional
    public CommunityPostResponseDTO updatePost(Long postId, CommunityPostUpdateDTO postUpdateDTO) {
        User user = getCurrentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to update this post");
        }

        post.setTitle(postUpdateDTO.getTitle());
        post.setDescription(postUpdateDTO.getDescription());

        post = postRepository.save(post);
        return mapToPostResponseDTO(post);
    }

    // This method is used to delete a post
    @Transactional
    public void deletePost(Long postId) {
        User user = getCurrentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to delete this post");
        }

        // Delete all comments associated with the post
        commentRepository.deleteAll(commentRepository.findByPostId(postId));
        postRepository.delete(post);
    }

    // This method is used to add a comment to a post
    @Transactional
    public CommunityCommentResponseDTO addComment(Long postId, CommunityCommentDTO commentDTO) {
        User user = getCurrentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(commentDTO.getContent());

        comment = commentRepository.save(comment);
        return mapToCommentResponseDTO(comment);
    }

    // This method is used to update a comment
    @Transactional
    public CommunityCommentResponseDTO updateComment(Long commentId, CommunityCommentDTO commentDTO) {
        User user = getCurrentUser();
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        comment = commentRepository.save(comment);
        return mapToCommentResponseDTO(comment);
    }

    // This method is used to delete a comment
    @Transactional
    public void deleteComment(Long commentId) {
        User user = getCurrentUser();
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // This method is used to get comments by post ID
    public List<CommunityCommentResponseDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToCommentResponseDTO)
                .collect(Collectors.toList());
    }


    // Helper methods to map entities to DTOs
    private CommunityPostResponseDTO mapToPostResponseDTO(CommunityPost post) {
        CommunityPostResponseDTO dto = new CommunityPostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setUserId(post.getUser().getId());
        dto.setUsername(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setCommentCount(commentRepository.countByPostId(post.getId()));
        return dto;
    }

    // Helper method to map comment entity to DTO
    private CommunityCommentResponseDTO mapToCommentResponseDTO(CommunityComment comment) {
        CommunityCommentResponseDTO dto = new CommunityCommentResponseDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        }
        throw new IllegalStateException("User not authenticated");
    }
}