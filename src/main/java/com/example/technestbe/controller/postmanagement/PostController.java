package com.example.technestbe.controller.postmanagement;

import com.example.technestbe.dto.postmanagement.*;
import com.example.technestbe.service.postmanagement.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @ModelAttribute PostCreateDTO postCreateDTO) throws Exception {
        PostResponseDTO post = postService.createPost(postCreateDTO);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PostResponseDTO>> getUserPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getUserPosts(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostUpdateDTO postUpdateDTO) throws Exception {
        PostResponseDTO post = postService.updatePost(id, postUpdateDTO);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) throws Exception {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id, @Valid @RequestBody LikeDTO likeDTO) {
        likeDTO.setPostId(id);
        postService.likePost(likeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Void> commentPost(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO) {
        postService.commentPost(id, commentDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Void> sharePost(@PathVariable Long id, @Valid @RequestBody ShareDTO shareDTO) {
        shareDTO.setPostId(id);
        postService.sharePost(shareDTO);
        return ResponseEntity.ok().build();
    }
}