package mytrophy.api.game.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "app_id"})
        }
)
public class GameReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", referencedColumnName = "appId", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus reviewStatus;

    @Builder
    public GameReview(Member member, Game game, ReviewStatus reviewStatus) {
        this.member = member;
        this.game = game;
        this.reviewStatus = reviewStatus;
    }

    public void updateReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
