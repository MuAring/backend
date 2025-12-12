package com.example.muaring.domain.social.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupPlaylist;
import com.example.muaring.domain.group.level.GroupActivityType;
import com.example.muaring.domain.group.level.GroupLevelService;
import com.example.muaring.domain.group.repository.GroupPlaylistRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.social.dto.post.*;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GroupRepository groupRepository;
    private final GroupPlaylistRepository groupPlaylistRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final MusicService musicService;
    private final GroupLevelService groupLevelService;

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

            MusicPost personalPost = MusicPost.builder()
                    .member(member)
                    .music(music)
                    .group(null)
                    .isProfile(true)
                    .content(request.getContent())
                    .likeCount(0)
                    .commentCount(0)
                    .build();

            musicPostRepository.save(personalPost);

            // 그룹 레벨 EXP 반영 (오늘의 음악)
            groupLevelService.addActivity(
                    group.getId(),
                    memberId,
                    GroupActivityType.TODAY_MUSIC_POST
            );
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
                .postId(post.getId())
                .musicId(post.getMusic().getId())
                .title(post.getMusic().getName())
                .artist(post.getMusic().getArtistName())
                .albumImage(post.getMusic().getAlbumImgUrl())
                .createdAt(post.getCreatedAt())
                .build());
    }

    @Transactional(readOnly = true)
    public Page<MusicPostFeedResponseDto> getTodayFolloweePosts(Pageable pageable) {

        Long memberId = SecurityUtil.getMemberId();

        // Pagination + sort 된 채로 바로 가져오기
        Pageable pageableNoSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        Page<MusicPost> posts = musicPostRepository.findTodayPostsByFollowees(memberId, pageableNoSort);
        return posts.map(MusicPostFeedResponseDto::from);
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
