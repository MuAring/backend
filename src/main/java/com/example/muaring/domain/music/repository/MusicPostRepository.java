package com.example.muaring.domain.music.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicPostRepository extends JpaRepository<MusicPost, Long> {

    List<MusicPost> findByMemberId(Long memberId);
}
