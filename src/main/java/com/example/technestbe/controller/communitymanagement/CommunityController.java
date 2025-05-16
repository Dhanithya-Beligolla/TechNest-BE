package com.example.technestbe.controller.communitymanagement;

import com.example.technestbe.dto.communitymanagement.*;
import com.example.technestbe.service.communitymanagement.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/posts")
    public ResponseEntity<CommunityPostResponseDTO> createPost(@Valid @RequestBody CommunityPostCreateDTO postCreateDTO) {
        CommunityPostResponseDTO post = communityService.createPost(postCreateDTO);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<CommunityPostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(communityService.getAllPosts(pageable));
    }

    @GetMapping("/posts/user")
    public ResponseEntity<Page<CommunityPostResponseDTO>> getUserPosts(Pageable pageable) {
        return ResponseEntity.ok(communityService.getUserPosts(pageable));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<CommunityPostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody CommunityPostUpdateDTO postUpdateDTO) {
        CommunityPostResponseDTO post = communityService.updatePost(id, postUpdateDTO);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommunityCommentResponseDTO> addComment(@PathVariable Long id, @Valid @RequestBody CommunityCommentDTO commentDTO) {
        CommunityCommentResponseDTO comment = communityService.addComment(id, commentDTO);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommunityCommentResponseDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommunityCommentDTO commentDTO) {
        CommunityCommentResponseDTO comment = communityService.updateComment(id, commentDTO);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        communityService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommunityCommentResponseDTO>> getCommentsByPostId(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommentsByPostId(id));
    }
}