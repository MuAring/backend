package com.example.muaring.domain.nearby.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberLocationQueryRepository;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.nearby.dto.TodayNearbyMusicDTO;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NearbyService {

    private final MemberLocationQueryRepository locationQueryRepository;
    private final MusicPostRepository musicPostRepository;

    public List<TodayNearbyMusicDTO> getTodayMusicOfNearbyUsers(double lat, double lng, double radiusKm){

        Long memberId = SecurityUtil.getMemberId();

        List<Long> nearbyUserIds = locationQueryRepository.findNearbyUserIdsOnly(lat, lng, memberId, radiusKm);

        List<TodayNearbyMusicDTO> results = new ArrayList<>();

        for (Long userId : nearbyUserIds) {
            musicPostRepository.findTodayPostByMember(userId).ifPresent(post -> {

                Music music = post.getMusic();
                Member member = post.getMember();

                results.add(
                        TodayNearbyMusicDTO.builder()
                                .memberId(member.getId())
                                .profileImageUrl(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null
                                )
                                .musicName(music.getName())
                                .artistName(music.getArtistName())
                                .albumImageUrl(music.getAlbumImgUrl())
                                .build()
                );
            });
        }
        return results;

    }
}



