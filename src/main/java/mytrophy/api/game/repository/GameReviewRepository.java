package mytrophy.api.game.repository;

import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameReviewRepository extends JpaRepository<GameReview, Long> {

    //특정 회원의 평가들
    List<GameReview> findByMember(Member member);

    //특정 게임의 평가들
    List<GameReview> findByGame(Game game);

    //특정 회원의 특정 게임에 대한 평가
    Optional<GameReview> findByMemberAndGame(Member member, Game game);

    //특정 회원의 평가 (평가기준별)
    List<GameReview> findByMemberAndReviewStatus(Member member, ReviewStatus reviewStatus);

}
