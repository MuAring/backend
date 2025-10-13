package com.example.muaring.domain.user;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_artist_preference",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "artist_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserArtistPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_artist_id")
    private Long userArtistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "artist_id", nullable = false)
    private String artistId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
}
