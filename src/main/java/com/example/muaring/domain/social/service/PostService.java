package com.example.muaring.domain.social.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.dto.PostDetailReadResponse;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupPlaylist;
import com.example.muaring.domain.group.level.GroupActivityType;
import com.example.muaring.domain.group.level.GroupLevelService;
import com.example.muaring.domain.group.repository.GroupPlaylistRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.library.repository.LibraryRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.service.MemberService;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.social.dto.post.*;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.exception.post.PostException;
import com.example.muaring.domain.social.repository.LikeRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final GroupRepository groupRepository;
    private final GroupPlaylistRepository groupPlaylistRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final MusicService musicService;
    private final GroupLevelService groupLevelService;
    private final LibraryRepository libraryRepository;
    private final LikeRepository likeRepository;
    private final MemberService memberService;

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

    public Page<MusicPostFeedResponseDto> getTodayFolloweePosts(Pageable pageable) {

        Long viewerId = SecurityUtil.getMemberId();

        if (viewerId == null) {
            throw new PostException(PostErrorCode.UNAUTHROIZED);
        }

        // Pagination + sort 된 채로 바로 가져오기
        Pageable pageableNoSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        Page<MusicPost> posts = musicPostRepository.findTodayPostsByFollowees(viewerId, pageableNoSort);
        return mapToFeedDtosWithLibraryFlag(posts, viewerId);
    }
    
    // 내가 공유한 게시물만 조회
    public Page<MusicPostFeedResponseDto> getMyPosts(Pageable pageable) {

        Long viewerId = SecurityUtil.getMemberId();

        if (viewerId == null) {
            throw new PostException(PostErrorCode.UNAUTHROIZED);
        }

        // Pagination + sort 된 채로 바로 가져오기
        Pageable pageableNoSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        Page<MusicPost> posts = musicPostRepository.findMyPosts(viewerId, pageableNoSort);
        return mapToFeedDtosWithLibraryFlag(posts, viewerId);
    }

    // 피드에 보관함 여부 + 좋아요 눌렀는지 여부 추가
    private Page<MusicPostFeedResponseDto> mapToFeedDtosWithLibraryFlag(
            Page<MusicPost> posts,
            Long viewerId
    ) {

        List<MusicPost> content = posts.getContent();
        if (content.isEmpty()) {
            return posts.map(post -> MusicPostFeedResponseDto.from(post, false, false));
        }

        // postIds, musicIds 추출
        List<Long> postIds = content.stream()
                .map(MusicPost::getId)
                .toList();

        List<Long> musicIds = content.stream()
                .map(p -> p.getMusic().getId())
                .toList();

        // 내 보관함에 있는 musicId 목록 조회
        List<Long> inLibraryIds = libraryRepository.findMusicIdsInLibrary(viewerId, musicIds);
        Set<Long> inLibrarySet = new HashSet<>(inLibraryIds);

        // 내가 좋아요 누른 postId 목록 조회
        List<Long> likedPostIds = likeRepository.findLikedPostIds(viewerId, postIds);
        Set<Long> likedSet = new HashSet<>(likedPostIds);

        // 같은 멤버가 여러 게시글을 쓸 수 있으니까 presigned URL 캐싱
        Map<Long, String> profileUrlCache = new HashMap<>();

        return posts.map(post -> {
            boolean inLibrary = inLibrarySet.contains(post.getMusic().getId());
            boolean liked = likedSet.contains(post.getId());

            Member member = post.getMember();
            Long memberId = member.getId();

            // presigned URL 캐싱해서 member당 한 번만 생성
            String profileImageUrl = profileUrlCache.computeIfAbsent(
                    memberId,
                    id -> memberService.resolveProfileImageUrl(member)
            );

            return MusicPostFeedResponseDto.from(post, inLibrary, liked, profileImageUrl);
        });
    }

    public TodayPostResponseDTO getTodayPostByMember(Long memberId) {

        // 멤버 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        // 오늘의 게시물 조회
        MusicPost post = musicPostRepository.findTodayPostByMember(memberId)
                .orElseThrow(() -> new MusicException(PostErrorCode.POST_NOT_FOUND));

        Music music = post.getMusic();

        // DTO 반환
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

    // 게시물 상세 조회 (댓글 제외. 댓글은 댓글 조회 api로 프론트에서 따로 불러오게 함)
    public PostDetailReadResponse getPostDetail(Long postId, Long memberId) {
        // 게시물 조회
        MusicPost post = musicPostRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

//        // 그룹 게시물인 경우 접근 권한 확인
//        if (post.getGroup() != null && !post.getGroup().getIsPublic()) {
//            boolean isMember = groupMemberRepository.existsByGroupIdAndMemberId(
//                    post.getGroup().getId(),
//                    memberId
//            );
//            if (!isMember) {
//                throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
//            }
//        }

        // 현재 사용자가 좋아요를 눌렀는지 확인
        boolean isLiked = likeRepository.existsByPostIdAndMemberId(postId, memberId);

        // 현재 게시글의 음악이 이미 보관함에 존재하는지 확인
        boolean isAlreadyInLibrary = libraryRepository.existsByMemberIdAndMusicId(memberId, post.getMusic().getId());
        return PostDetailReadResponse.of(post, isLiked, isAlreadyInLibrary);
    }
}
