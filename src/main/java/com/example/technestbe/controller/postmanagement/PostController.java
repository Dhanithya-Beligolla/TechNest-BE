package com.example.technestbe.controller.postmanagement;

import com.example.technestbe.dto.postmanagement.*;
import com.example.technestbe.service.postmanagement.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    //create that end point to create a post
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @ModelAttribute PostCreateDTO postCreateDTO) throws Exception {
        PostResponseDTO post = postService.createPost(postCreateDTO);  // call the createPost function in the postService service
        return ResponseEntity.ok(post);
    }

    //create that end point to get all of the post
    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    //create that end point to get the logged user's posts
    @GetMapping("/user")
    public ResponseEntity<Page<PostResponseDTO>> getUserPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getUserPosts(pageable));
    }

    //create that end point to update the post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostUpdateDTO postUpdateDTO) throws Exception {
        PostResponseDTO post = postService.updatePost(id, postUpdateDTO);
        return ResponseEntity.ok(post);
    }

    //create that end point to delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) throws Exception {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //create that end point to like a post
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id, @Valid @RequestBody LikeDTO likeDTO) {
        likeDTO.setPostId(id);
        postService.likePost(likeDTO);
        return ResponseEntity.ok().build();
    }

    // New endpoint to unlike a post
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long id) {
        postService.unlikePost(id);
        return ResponseEntity.ok().build();
    }

    // New endpoint to check if the user has liked the post
    @GetMapping("/{id}/has-liked")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable Long id) {
        boolean hasLiked = postService.hasUserLikedPost(id);
        return ResponseEntity.ok(hasLiked);
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

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getPostComments(@PathVariable Long id) {
        List<CommentDTO> comments = postService.getPostComments(id);
        return ResponseEntity.ok(comments);
    }
}