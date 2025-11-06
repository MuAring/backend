package com.example.muaring.domain.social.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import com.example.muaring.domain.social.entity.MusicPost;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final MusicService musicService;

    @Transactional
    public Page<MusicHistoryDTO> getMusicHistoryByMember(Integer year, Integer month, Pageable pageable) {

        Long memberId = SecurityUtil.getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        Page<MusicPost> posts = musicPostRepository.findByMemberAndYearMonth(memberId, year, month, pageable);

        return posts.map(post -> MusicHistoryDTO.builder()
                        .musicId(post.getMusic().getId())
                        .title(post.getMusic().getName())
                        .artist(post.getMusic().getArtistName())
                        .albumImage(post.getMusic().getAlbumImgUrl())
                        .createdAt(post.getCreatedAt())
                        .build());
    }

    @Transactional
    public MusicPost createMusicPost(Long groupId, String spotifyId, String content) {

        Long memberId = SecurityUtil.getMemberId();

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