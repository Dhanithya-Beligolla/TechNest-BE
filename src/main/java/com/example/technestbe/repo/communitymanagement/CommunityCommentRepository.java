package com.example.technestbe.repo.communitymanagement;

import com.example.technestbe.model.communitymanagement.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByPostId(Long postId);

    Long countByPostId(Long postId);
}