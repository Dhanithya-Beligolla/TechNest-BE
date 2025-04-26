package com.example.technestbe.repo.postmanagement;

import com.example.technestbe.model.post.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Long> {
    long countByPostId(Long postId);
}