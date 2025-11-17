package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "replies")
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
