package mytrophy.api.comment.service;


import lombok.RequiredArgsConstructor;
import mytrophy.api.article.entity.Article;
import mytrophy.api.article.repository.ArticleRepository;
import mytrophy.api.comment.dto.CommentDto;
import mytrophy.api.comment.dto.CreateCommentDto;
import mytrophy.api.comment.entity.Comment;
import mytrophy.api.comment.entity.CommentLike;
import mytrophy.api.comment.repository.CommentLikeRepository;
import mytrophy.api.comment.repository.CommentRepository;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final CommentLikeRepository commentLikeRepository;

    //댓글 등록
    @Override
    public CommentDto createComment(Long memberId, Long articleId, CreateCommentDto createCommentDto, Long parentCommentId) {
        Member member = findMemberById(memberId);
        Article article = findArticleById(articleId);

        Comment parentComment = null;

        if (parentCommentId != null) {
            parentComment = findParentComment(parentCommentId);
            validateParentComment(parentComment);
        }

        Comment comment = dtoToEntity(createCommentDto, member, article, parentComment);
        Comment createdComment = commentRepository.save(comment);
        return entityToDto(createdComment);
    }

    //댓글 수정
    @Override
    public CommentDto updateComment(Long commentId, Long memberId, String content) {
        Comment comment = findCommentById(commentId);
        validateAuthorization(comment, memberId);

        comment.updateContent(content);
        return entityToDto(commentRepository.save(comment));
    }

    //댓글 삭제
    @Override
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findCommentById(commentId);
        validateAuthorization(comment, memberId);

        if (!comment.getChildrenComment().isEmpty()) {
            commentRepository.deleteAllByParentComment(comment);
        }

        commentRepository.delete(comment);
    }

    //특정 회원의 댓글 전체조회
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByMemberId(Long memberId) {
        List<Comment> comments = commentRepository.findByMemberId(memberId);
        return comments.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    //댓글 좋아요
    @Override
    public void toggleLikeComment(Long commentId, Long memberId) {
        Comment comment = findCommentById(commentId);
        Member member = findMemberById(memberId);

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndMember(comment, member);

        if(existingLike.isPresent()) {
            // 좋아요 취소
            commentLikeRepository.delete(existingLike.get());
            comment.decrementLikes();
        } else {
            // 좋아요
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .member(member)
                    .build();
            commentLikeRepository.save(commentLike);
            comment.incrementLikes();
        }

        commentRepository.save(comment);
    }

    //댓글 id로 댓글 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));
    }

    //회원 id로 회원 조회
    private Member findMemberById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));
    }

    //게시글 id로 게시글 조회
    private Article findArticleById(Long articleId){
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_ARTICLE_ID));
    }

    //부모 댓글 id로 부모 댓글 조회
    private Comment findParentComment(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_PARENT_COMMENT_ID));
    }

    //부모 댓글 유효성 검사
    private void validateParentComment(Comment parentComment) {
        if (parentComment.getParentComment() != null) {
            throw new CustomException(ErrorCodeEnum.NOT_PARENT_COMMENT);
        }
    }

    //권한 확인
    private void validateAuthorization(Comment comment, Long memberId){
        if (!comment.getMember().getId().equals(memberId)){
            throw new CustomException(ErrorCodeEnum.UNAUTHORIZED);
        }
    }

    //entity -> dto
    private CommentDto entityToDto(Comment comment) {
        return new CommentDto(comment);
    }

    //dto -> entity
    private Comment dtoToEntity(CreateCommentDto createCommentDto, Member member, Article article, Comment parentComment) {
        return Comment.builder()
                .content(createCommentDto.getContent())
                .member(member)
                .article(article)
                .parentComment(parentComment)
                .build();
    }
}
