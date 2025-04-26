package com.example.technestbe.repo.communitymanagement;

import com.example.technestbe.model.communitymanagement.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    Page<CommunityPost> findByUserId(Long userId, Pageable pageable);
}