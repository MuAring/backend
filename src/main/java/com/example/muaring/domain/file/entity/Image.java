package com.example.muaring.domain.file.entity;

import com.example.muaring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "image_type", length = 255, nullable = false)
    private ImageType type;

    @Column(name = "s3_key", length = 255, nullable = false)
    private String s3Key;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    public Image(String fileName, ImageType type, String s3Key, Long fileSize) {
        this.fileName = fileName;
        this.type = type;
        this.s3Key = s3Key;
        this.fileSize = fileSize;
    }

    public static Image create(String fileName, ImageType type, String s3Key, Long fileSize) {
        return new Image(fileName, type, s3Key, fileSize);
    }
}
