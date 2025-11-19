package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupPlaylist;
import com.example.muaring.domain.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupPlaylistRepository extends JpaRepository<GroupPlaylist, Long> {

    @Query("select count(gp) from GroupPlaylist gp where gp.group.id = :groupId")
    int countByGroupId(@Param("groupId") Long groupId);

    boolean existsByGroupAndMusic(Group group, Music music);

}