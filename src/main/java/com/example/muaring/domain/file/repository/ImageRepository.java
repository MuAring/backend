package com.example.muaring.domain.file.repository;

import com.example.muaring.domain.file.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}