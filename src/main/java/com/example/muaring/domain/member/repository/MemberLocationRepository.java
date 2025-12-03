package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.MemberLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
}


