package com.example.technestbe.repo.postmanagement;

import com.example.technestbe.model.post.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId); // New method for unliking
}