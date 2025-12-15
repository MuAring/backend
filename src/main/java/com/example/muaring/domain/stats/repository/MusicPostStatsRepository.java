package com.example.muaring.domain.stats.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MusicPostStatsRepository extends Repository<MusicPost, Long> {

    interface TopMusicRow {
        String getTitle();
        String getArtistName();
        String getAlbumImgUrl();
        Long getShareCount();
    }

    @Query(value = """
        SELECT
            m.name AS title,
            m.artist_name AS artistName,
            m.album_img_url AS albumImgUrl,
            COUNT(*) AS shareCount
        FROM music_post mp
        JOIN music m ON m.music_id = mp.music_id
        WHERE mp.is_deleted = false
          AND mp.created_at >= :start
          AND mp.created_at < :end
        GROUP BY m.name, m.artist_name, m.album_img_url
        ORDER BY shareCount DESC
        LIMIT 3
    """, nativeQuery = true)
    List<TopMusicRow> findTop3ByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
