package com.example.muaring.domain.file.repository;

import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.entity.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {

    Optional<Image> findByImageTypeAndS3Key(ImageType imageType, String s3Key);
}