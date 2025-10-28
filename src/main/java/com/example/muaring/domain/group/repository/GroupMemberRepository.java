package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @EntityGraph(attributePaths = { "group" })
    List<GroupMember> findByMember_IdOrderByGroup_NameAsc(Long memberId);
}
