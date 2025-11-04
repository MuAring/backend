package com.example.muaring.config;

import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultImageInitializer {

    private final ImageRepository imageRepository;
    private final ImageProperties imageProperties;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        ImageProperties.DefaultImageProperties profileImage = imageProperties.defaultProfile();

        boolean exists = imageRepository.findByImageTypeAndS3Key(ImageType.MEMBER, profileImage.s3Key()).isPresent();
        if (exists) {
            log.info("✅ 회원 프로필 기본 이미지가 이미 존재합니다.");
            return;
        }

        Image defaultMemberImage = Image.create(
                profileImage.fileName(),
                profileImage.fileType(),
                ImageType.MEMBER,
                profileImage.s3Key(),
                profileImage.fileSize()
        );

        imageRepository.save(defaultMemberImage);
        log.info("✅ 회원 프로필 기본 이미지가 생성되었습니다: {}", defaultMemberImage.getS3Key());
    }
}