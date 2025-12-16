package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.MemberLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberLocationQueryRepository extends JpaRepository<MemberLocation, Long> {

    @Query(value = """
    SELECT sub.member_id
    FROM (
        SELECT 
            ml.member_id,
            (
                6371 * acos(
                    cos(radians(:lat)) *
                    cos(radians(ml.latitude)) *
                    cos(radians(ml.longitude) - radians(:lng)) +
                    sin(radians(:lat)) *
                    sin(radians(ml.latitude))
                )
            ) AS distance
        FROM member_location ml
        WHERE ml.member_id <> :selfId
                AND ml.updated_at >= NOW() - INTERVAL '5 minutes'
    ) AS sub
    WHERE sub.distance < :radius
    ORDER BY sub.distance
    """, nativeQuery = true)

    List<Long> findNearbyUserIdsOnly(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("selfId") Long selfId,
            @Param("radius") double radiusKm
    );
}

