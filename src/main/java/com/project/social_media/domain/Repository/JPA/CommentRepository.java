package com.project.social_media.domain.Repository.JPA;

import com.project.social_media.domain.Model.JPA.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentByCommentId(Long commentId);
}
