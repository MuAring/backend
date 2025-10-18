package com.example.muaring.domain.group;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.Image;
import com.example.muaring.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

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
}
