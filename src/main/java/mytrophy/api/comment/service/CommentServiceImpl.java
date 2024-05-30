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
    public CommentDto createComment(Long memberId, Long articleId, CreateCommentDto createCommentDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_ARTICLE_ID));

        Comment comment = dtoToEntity(createCommentDto, member,article);
        Comment createdComment = commentRepository.save(comment);
        return entityToDto(createdComment);
    }

    //댓글 수정
    @Override
    public CommentDto updateComment(Long commentId, Long memberId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));

        if (!isAuthorized(commentId, memberId)) {
            throw new CustomException(ErrorCodeEnum.UNAUTHORIZED);
        }

        comment.updateContent(content);
        return entityToDto(commentRepository.save(comment));
    }

    //댓글 삭제
    @Override
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));

        if (!isAuthorized(commentId, memberId)) {
            throw new CustomException(ErrorCodeEnum.UNAUTHORIZED);
        }

        commentRepository.delete(comment);
    }

    //특정 게시글의 댓글 전체조회
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByArticleId(Long articleId) {
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        return comments.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
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

    //게시글 별 댓글 수 조회
    @Override
    @Transactional(readOnly = true)
    public int countByArticleId(Long articleId) {
        return commentRepository.countByArticleId(articleId);
    }

    //댓글 추천
    @Override
    public void likeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndMember(comment, member);
        if(existingLike.isPresent()) {
            throw new CustomException(ErrorCodeEnum.ALREADY_LIKED_COMMENT_ID);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
        commentLikeRepository.save(commentLike);

        comment.incrementCntUp();
        commentRepository.save(comment);
    }

    //댓글 추천 취소
    @Override
    public void unlikeComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        CommentLike commentLike = commentLikeRepository.findByCommentAndMember(comment, member)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_LIKED_COMMENT_ID));

        commentLikeRepository.delete(commentLike);
        comment.decrementCntUp();
        commentRepository.save(comment);
    }

    //권한 확인
    @Override
    public boolean isAuthorized(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_COMMENT_ID));
        return comment.getMember().getId().equals(memberId);
    }

    //entity -> dto
    private CommentDto entityToDto(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getContent(),
            comment.getMember().getId(),
            comment.getArticle().getId(),
            comment.getCntUp()
        );
    }

    //dto -> entity
    private Comment dtoToEntity(CreateCommentDto createCommentDto, Member member, Article article) {
        return Comment.builder()
                .content(createCommentDto.getContent())
                .member(member)
                .article(article)
                .build();
    }
}
