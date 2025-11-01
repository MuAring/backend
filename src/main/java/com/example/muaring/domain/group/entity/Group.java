package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member admin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isPublic = true;

    @Column(name = "playlist_updated_at")
    private LocalDateTime playlistUpdatedAt;

    // Group 삭제 시 모든 GroupMember도 함께 삭제하기 위해 GroupMember와의 양방향 관계 + CASCADE 설정
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @Builder
    public Group(Member admin, String name, String description, Integer maxMembers, Boolean isPublic) {
        this.admin = admin;
        this.name = name;
        this.description = description;
        this.memberCount = 1; // 그룹 생성 시 멤버 수는 1명
        this.maxMembers = maxMembers;
        this.isPublic = isPublic;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
