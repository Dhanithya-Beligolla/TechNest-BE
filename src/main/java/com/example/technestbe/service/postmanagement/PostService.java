package com.example.technestbe.service.postmanagement;

import com.example.technestbe.config.usermanagement.CustomUserDetails; // Added import
import com.example.technestbe.dto.postmanagement.*;
import com.example.technestbe.model.post.*;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.postmanagement.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Added import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public PostResponseDTO createPost(PostCreateDTO postCreateDTO) throws IOException {
        User user = getCurrentUser();
        Post post = new Post();
        post.setTitle(postCreateDTO.getTitle());
        post.setDescription(postCreateDTO.getDescription());
        post.setUser(user);

        if (postCreateDTO.getMediaFiles() != null && postCreateDTO.getMediaFiles().length <= 3) {
            List<String> mediaUrls = cloudinaryService.uploadFiles(postCreateDTO.getMediaFiles());
            post.setMediaUrls(mediaUrls);
        }

        post = postRepository.save(post);
        return mapToResponseDTO(post);
    }

    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public Page<PostResponseDTO> getUserPosts(Pageable pageable) {
        User user = getCurrentUser();
        return postRepository.findByUserId(user.getId(), pageable).map(this::mapToResponseDTO);
    }

    @Transactional
    public PostResponseDTO updatePost(Long postId, PostUpdateDTO postUpdateDTO) throws IOException {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to update this post");
        }

        post.setTitle(postUpdateDTO.getTitle());
        post.setDescription(postUpdateDTO.getDescription());

        if (postUpdateDTO.getMediaFiles() != null && postUpdateDTO.getMediaFiles().length <= 3) {
            // Delete old media
            cloudinaryService.deleteFiles(post.getMediaUrls());
            // Upload new media
            List<String> mediaUrls = cloudinaryService.uploadFiles(postUpdateDTO.getMediaFiles());
            post.setMediaUrls(mediaUrls);
        }

        post = postRepository.save(post);
        return mapToResponseDTO(post);
    }

    @Transactional
    public void deletePost(Long postId) throws IOException {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to delete this post");
        }

        cloudinaryService.deleteFiles(post.getMediaUrls());
        postRepository.delete(post);
    }

    @Transactional
    public void likePost(LikeDTO likeDTO) {
        User user = getCurrentUser();
        Post post = postRepository.findById(likeDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            throw new IllegalStateException("User already liked this post");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void commentPost(Long postId, CommentDTO commentDTO) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
    }

    @Transactional
    public void sharePost(ShareDTO shareDTO) {
        User user = getCurrentUser();
        Post post = postRepository.findById(shareDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Share share = new Share();
        share.setPost(post);
        share.setUser(user);
        shareRepository.save(share);
    }

    private PostResponseDTO mapToResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setUserId(post.getUser().getId());
        dto.setUsername(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        dto.setMediaUrls(post.getMediaUrls());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikeCount(likeRepository.countByPostId(post.getId()));
        dto.setCommentCount(commentRepository.countByPostId(post.getId()));
        dto.setShareCount(shareRepository.countByPostId(post.getId()));
        return dto;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        }
        throw new IllegalStateException("User not authenticated");
    }
}