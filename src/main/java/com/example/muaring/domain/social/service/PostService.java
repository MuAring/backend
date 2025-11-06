package com.example.muaring.domain.social.service;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.dto.MusicRequestDTO;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.response.SpotifyTrackDetailResponse;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.music.service.SpotifyAuthService;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import com.example.muaring.domain.music.repository.MusicRepository;
import com.example.muaring.domain.music.response.SpotifySearchResponse;
import com.example.muaring.domain.social.entity.MusicPost;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final MusicService musicService;

    @Transactional
    public List<MusicHistoryDTO> getMusicHistoryByMember(Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        List<MusicPost> posts = musicPostRepository.findByMemberId(memberId);

        return posts.stream()
                .map(post -> MusicHistoryDTO.builder()
                        .musicId(post.getMusic().getId())
                        .title(post.getMusic().getName())
                        .artist(post.getMusic().getArtistName())
                        .albumImage(post.getMusic().getAlbumImgUrl())
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public MusicPost createMusicPost(Long memberId, Long groupId, String spotifyId, String content) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));

        Group group = null;
        if (groupId != null) {
            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new MusicException(MusicErrorCode.GROUP_NOT_FOUND));
        }

        Music music = musicService.findOrCreateMusic(spotifyId);

        MusicPost post = MusicPost.builder()
                .member(member)
                .music(music)
                .group(group)
                .isProfile(true)
                .content(content)
                .likeCount(0)
                .commentCount(0)
                .build();

        return musicPostRepository.save(post);
    }
}