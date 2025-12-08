package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMusicProfileRepository extends JpaRepository<GroupMusicProfile, Long> {

    List<GroupMusicProfile> findByStatus(ProfileStatus status);
}
