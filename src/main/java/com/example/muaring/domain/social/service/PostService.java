package com.example.muaring.domain.social.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupPlaylist;
import com.example.muaring.domain.group.repository.GroupPlaylistRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.social.dto.post.MusicPostDTO;
import com.example.muaring.domain.social.dto.post.MusicPostListResponseDTO;
import com.example.muaring.domain.social.dto.post.MusicPostRequestDTO;
import com.example.muaring.domain.social.dto.post.TodayPostResponseDTO;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GroupRepository groupRepository;
    private final GroupPlaylistRepository groupPlaylistRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final MusicService musicService;

    @Transactional
    public MusicPostDTO createMusicPost(MusicPostRequestDTO request) {

        Long memberId = SecurityUtil.getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));

        Group group = null;
        if (request.getGroupId() != null) {
            group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new MusicException(MusicErrorCode.GROUP_NOT_FOUND));
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        boolean alreadyPostedToday = musicPostRepository.existsTodayPostByMember(
                memberId, startOfDay, endOfDay);

        if (alreadyPostedToday) {
            throw new MusicException(PostErrorCode.ALREADY_POSTED_TODAY);
        }

        Music music = musicService.findOrCreateMusic(request.getSpotifyId());

        MusicPost post = MusicPost.builder()
                .member(member)
                .music(music)
                .group(group)
                .isProfile(true)
                .content(request.getContent())
                .likeCount(0)
                .commentCount(0)
                .build();

        MusicPost savedPost = musicPostRepository.save(post);

        if (group != null) {
            addMusicToGroupPlaylist(group, music);
        }

        return MusicPostDTO.builder()
                .postId(savedPost.getId())
                .memberId(member.getId())
                .groupId(group != null ? group.getId() : null)
                .spotifyId(music.getSpotifyId())
                .content(savedPost.getContent())
                .build();
    }

    private void addMusicToGroupPlaylist(Group group, Music music) {
        if (groupPlaylistRepository.existsByGroupAndMusic(group, music)) {
            return;
        }

        GroupPlaylist groupPlaylist = GroupPlaylist.builder()
                .group(group)
                .music(music)
                .createdAt(LocalDateTime.now())
                .build();

        groupPlaylistRepository.save(groupPlaylist);
    }

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

    @Transactional(readOnly = true)
    public List<MusicPostListResponseDTO> getTodayFolloweePosts() {

        Long memberId = SecurityUtil.getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        List<MusicPost> posts = musicPostRepository.findTodayPostsByFollowees(memberId);

        return posts.stream()
                .map(post -> MusicPostListResponseDTO.builder()
                        .postId(post.getId())
                        .memberId(post.getMember().getId())
                        .memberName(post.getMember().getNickname())
                        .profileImage(post.getMember().getProfileImage())
                        .content(post.getContent())
                        .albumImgUrl(post.getMusic().getAlbumImgUrl())
                        .musicName(post.getMusic().getName())
                        .artistName(post.getMusic().getArtistName())
                        .previewUrl(post.getMusic().getPreviewUrl())
                        .likeCount(post.getLikeCount().longValue())
                        .commentCount(post.getCommentCount().longValue())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public TodayPostResponseDTO getTodayPostByMember() {

        Long memberId = SecurityUtil.getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        MusicPost post = musicPostRepository.findTodayPostByMember(memberId)
                .orElseThrow(() -> new MusicException(PostErrorCode.POST_NOT_FOUND));

        Music music = post.getMusic();

        return TodayPostResponseDTO.builder()
                .postId(post.getId())
                .musicId(music.getId())
                .musicName(music.getName())
                .artistName(music.getArtistName())
                .albumImageUrl(music.getAlbumImgUrl())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }
}
