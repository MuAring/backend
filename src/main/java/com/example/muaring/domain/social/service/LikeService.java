package com.example.muaring.domain.social.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.social.dto.like.response.LikeResponseDTO;
import com.example.muaring.domain.social.entity.Like;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.exception.post.PostException;
import com.example.muaring.domain.social.repository.LikeRepository;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final MusicPostRepository postRepository;

    @Transactional
    public LikeResponseDTO handleLike(Long postId) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        MusicPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByPostIdAndMemberId(postId, memberId);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            return LikeResponseDTO.of(postId, post.getLikeCount(), false);
        } else {
            Like like = Like.create(post, member);
            likeRepository.save(like);
            post.increaseLikeCount();
            return LikeResponseDTO.of(postId, post.getLikeCount(), true);
        }
    }
}
