package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.member.entity.MemberMusicProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberMusicProfileRepository extends JpaRepository<MemberMusicProfile, Long> {

    List<MemberMusicProfile> findByStatus(ProfileStatus status);

}
