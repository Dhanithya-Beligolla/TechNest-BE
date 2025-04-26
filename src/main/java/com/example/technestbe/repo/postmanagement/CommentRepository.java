package com.example.technestbe.repo.postmanagement;

import com.example.technestbe.model.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(Long postId);
}