package com.example.muaring.domain.social.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.level.GroupActivityType;
import com.example.muaring.domain.group.level.GroupLevelService;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.social.dto.comment.request.CommentCreateRequest;
import com.example.muaring.domain.social.dto.comment.response.CommentReadResponse;
import com.example.muaring.domain.social.dto.comment.response.CommentResponse;
import com.example.muaring.domain.social.entity.Comment;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.exception.comment.CommentErrorCode;
import com.example.muaring.domain.social.exception.comment.CommentException;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.exception.post.PostException;
import com.example.muaring.domain.social.repository.CommentRepository;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final MusicPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final GroupLevelService groupLevelService;

    @Transactional
    public CommentResponse createComment(Long memberId, Long postId, CommentCreateRequest requestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        MusicPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.create(post, member, null, requestDTO.content());
        post.increaseCommentCount();
        commentRepository.save(comment);

        // 그룹 게시글이면 EXP 반영
        Group group = post.getGroup();
        if (group != null) {
            groupLevelService.addActivity(
                    group.getId(),
                    memberId,
                    GroupActivityType.COMMENT
            );
        }

        return CommentResponse.of(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (comment.getIsDeleted() == true) {
            throw new CommentException(CommentErrorCode.COMMENT_ALREADY_DELETED);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentResponse createReply(Long commentId, CommentCreateRequest requestDTO) {
        Long memberId = SecurityUtil.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
        if (parentComment.getIsDeleted() == true) {
            throw new CommentException(CommentErrorCode.CANNOT_REPLY_TO_DELETED_COMMENT);
        }

        MusicPost post = parentComment.getPost();
        Comment reply = Comment.create(post, member, parentComment, requestDTO.content());
        post.increaseCommentCount();
        commentRepository.save(reply);
        parentComment.getReplies().add(reply);

        // 대댓글도 그룹 EXP 반영 (COMMENT로 통일)
        Group group = post.getGroup();
        if (group != null) {
            groupLevelService.addActivity(
                    group.getId(),
                    memberId,
                    GroupActivityType.COMMENT
            );
        }

        return CommentResponse.of(reply);
    }

    public List<CommentReadResponse> getCommentsByPostId(Long postId) {
        MusicPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                .filter(c -> c.getParentComment() == null)  // 답글이 아닌 댓글만 필터링
                .filter(c -> {
                    boolean isParentDeleted = c.getIsDeleted();
                    boolean isAllRepliesDeleted = c.getReplies().stream().allMatch(Comment::getIsDeleted);  // 모든 답글이 삭제된 댓글만 필터링
                    return !(isParentDeleted && (c.getReplies().isEmpty() || isAllRepliesDeleted)); // 부모 댓글이 삭제되고, 답글이 없거나 모두 삭제된 경우를 제외한 댓글만 필터링

                })

                .map(CommentReadResponse::from)
                .toList();
    }
}
