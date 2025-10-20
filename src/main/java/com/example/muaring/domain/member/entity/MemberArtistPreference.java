package com.example.muaring.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_artist_preference",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "artist_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberArtistPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_artist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "artist_id", nullable = false)
    private String artistId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
}
