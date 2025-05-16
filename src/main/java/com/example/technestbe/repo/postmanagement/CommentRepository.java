package com.example.technestbe.repo.postmanagement;

import com.example.technestbe.model.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(Long postId);

    // New method to fetch comments by postId
    List<Comment> findByPostId(Long postId);

    // Optional: Useful for preventing duplicate comments by the same user
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}