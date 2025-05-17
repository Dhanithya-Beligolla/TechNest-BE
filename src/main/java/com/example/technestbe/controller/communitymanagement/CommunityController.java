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

    //this endpoint is use for creating a post
    @PostMapping("/posts")
    public ResponseEntity<CommunityPostResponseDTO> createPost(@Valid @RequestBody CommunityPostCreateDTO postCreateDTO) {
        CommunityPostResponseDTO post = communityService.createPost(postCreateDTO);
        return ResponseEntity.ok(post);
    }

    //this endpoint is use for getting all posts
    @GetMapping("/posts")
    public ResponseEntity<Page<CommunityPostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(communityService.getAllPosts(pageable)); //calling the service method to get all posts
    }

    @GetMapping("/posts/user")
    public ResponseEntity<Page<CommunityPostResponseDTO>> getUserPosts(Pageable pageable) {
        return ResponseEntity.ok(communityService.getUserPosts(pageable));  //calling the service method to get user posts
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<CommunityPostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody CommunityPostUpdateDTO postUpdateDTO) {
        CommunityPostResponseDTO post = communityService.updatePost(id, postUpdateDTO);  //calling the service method to update post
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id); //calling the service method to delete post
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommunityCommentResponseDTO> addComment(@PathVariable Long id, @Valid @RequestBody CommunityCommentDTO commentDTO) {
        CommunityCommentResponseDTO comment = communityService.addComment(id, commentDTO); //calling the service method to add comment
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommunityCommentResponseDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommunityCommentDTO commentDTO) {
        CommunityCommentResponseDTO comment = communityService.updateComment(id, commentDTO); //calling the service method to update comment
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        communityService.deleteComment(id); //calling the service method to delete comment
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommunityCommentResponseDTO>> getCommentsByPostId(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommentsByPostId(id));
    }
}