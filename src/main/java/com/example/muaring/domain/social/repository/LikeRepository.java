package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndMemberId(Long postId, Long memberId);
    void deleteByPostIdAndMemberId(Long postId, Long memberId);

    // 특정 게시물에 특정 회원이 좋아요를 눌렀는지 확인
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    // 좋아요 누른 게시물 목록
    @Query("select l.post.id from Like l where l.member.id = :memberId and l.post.id in :postIds")
    List<Long> findLikedPostIds(@Param("memberId") Long memberId,
                                @Param("postIds") List<Long> postIds);

}
