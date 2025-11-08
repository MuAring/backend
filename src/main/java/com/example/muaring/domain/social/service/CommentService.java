package com.example.muaring.domain.social.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.social.dto.comment.request.CommentCreateRequestDTO;
import com.example.muaring.domain.social.dto.comment.response.CommentResponseDTO;
import com.example.muaring.domain.social.entity.Comment;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.exception.PostErrorCode;
import com.example.muaring.domain.social.exception.PostException;
import com.example.muaring.domain.social.repository.CommentRepository;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final MusicPostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDTO createComment(Long postId, CommentCreateRequestDTO requestDTO) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        MusicPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.create(post, member, null, requestDTO.content());
        commentRepository.save(comment);

        return CommentResponseDTO.of(comment);
    }
}
