package com.example.muaring.domain.library.repository;

import com.example.muaring.domain.library.entity.Library;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    List<Library> findByMemberIdOrderByCreatedAtAsc(Long memberId);

    boolean existsByMemberIdAndMusicId(Long memberId, Long musicId);

    @Query("SELECT COUNT(l) > 0 FROM Library l WHERE l.id IN :ids AND l.member = :member")
    boolean existsByIdsAndMember(@Param("ids") List<Long> libraryIds, @Param("member") Member member);

    @Modifying
    @Query("DELETE FROM Library l WHERE l.id IN :ids AND l.member = :member")
    void deleteByIdsAndMember(@Param("ids") List<Long> libraryIds, @Param("member") Member member);

}