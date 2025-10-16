package com.example.muaring.domain.group;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.Image;
import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", nullable = false)
    private User adminUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_category_id", nullable = false)
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "playlist_updated_at")
    private LocalDateTime playlistUpdatedAt;
}
