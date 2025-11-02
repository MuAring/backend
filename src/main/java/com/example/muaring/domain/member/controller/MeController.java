package com.example.muaring.domain.member.controller;

import com.example.muaring.domain.group.dto.MyGroupListResponseDto;
import com.example.muaring.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final GroupService groupService;

    @GetMapping("/groups/{memberId}")
    public ResponseEntity<MyGroupListResponseDto> getMyGroups(@PathVariable Long memberId) {
        return ResponseEntity.ok(groupService.getMyGroups(memberId));
    }
}
