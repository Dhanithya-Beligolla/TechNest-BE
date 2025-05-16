package com.example.technestbe.service.postmanagement;

import com.example.technestbe.config.usermanagement.CustomUserDetails;
import com.example.technestbe.dto.postmanagement.*;
import com.example.technestbe.model.post.*;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.postmanagement.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
            cloudinaryService.deleteFiles(post.getMediaUrls());
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

        // Remove the exception to allow toggling in the frontend
        if (!likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    // New method to unlike a post
    @Transactional
    public void unlikePost(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Like like = likeRepository.findByPostIdAndUserId(post.getId(), user.getId())
                .orElseThrow(() -> new IllegalStateException("Like not found"));
        likeRepository.delete(like);
    }

    // New method to check if the user has liked the post
    public boolean hasUserLikedPost(Long postId) {
        User user = getCurrentUser();
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
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

    public List<CommentDTO> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(this::mapToCommentDTO).collect(Collectors.toList());
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

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
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