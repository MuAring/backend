package com.example.muaring.domain.library.repository;

import com.example.muaring.domain.library.entity.Library;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    // 현재 로그인한 멤버의 보관함 중, 해당 musicId 리스트에 포함되는 곡들의 musicId 들만 가져오기
    @Query("select l.music.id from Library l " +
            "where l.member.id = :memberId and l.music.id in :musicIds")
    List<Long> findMusicIdsInLibrary(
            @Param("memberId") Long memberId,
            @Param("musicIds") Collection<Long> musicIds
    );

}