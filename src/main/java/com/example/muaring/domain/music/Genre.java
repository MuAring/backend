package com.example.muaring.domain.music;

import jakarta.persistence.*;
import lombok.*;
import com.example.muaring.domain.common.BaseEntity;

@Entity
@Table(name = "genre")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long genreId;

    @Column(length = 50, nullable = false)
    private String name;
}
