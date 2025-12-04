package com.example.muaring.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_location")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLocation
{
    @Id
    private Long memberId;

    private Double latitude;
    private Double longitude;

    private LocalDateTime updatedAt;

    public void update(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        this.updatedAt = LocalDateTime.now();
    }
}
