package com.example.muaring.domain.music.service;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicResponseDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.repository.MusicPostRepository;
import com.example.muaring.domain.music.repository.MusicRepository;
import com.example.muaring.domain.social.entity.MusicPost;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicPostService {
    private final MusicRepository musicRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;

    public List<MusicResponseDTO> searchMusic(String query) {
        List<Music> musicList = musicRepository.findByNameContainingIgnoreCaseOrArtistNameContainingIgnoreCase(query, query);
        return musicList.stream()
                .map(music -> new MusicResponseDTO(
                        music.getId(),
                        music.getSpotifyId(),
                        music.getName(),
                        music.getArtistId(),
                        music.getArtistName(),
                        music.getAlbumName(),
                        music.getAlbumImgUrl(),
                        music.getPopularity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MusicPost createMusicPost(Long memberId, Long groupId, Long musicId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음악입니다."));
        Group group = null;
        if (groupId != null) {
            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
        }

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