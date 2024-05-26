package mytrophy.api.comment.repository;

import mytrophy.api.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleId(Long articleId);
    List<Comment> findByMemberId(Long memberId);
    int countByArticleId(Long articleId);

}
