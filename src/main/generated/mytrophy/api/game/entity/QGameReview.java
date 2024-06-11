package mytrophy.api.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGameReview is a Querydsl query type for GameReview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameReview extends EntityPathBase<GameReview> {

    private static final long serialVersionUID = 3336863L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGameReview gameReview = new QGameReview("gameReview");

    public final QGame game;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final mytrophy.api.member.entity.QMember member;

    public final EnumPath<mytrophy.api.game.enums.ReviewStatus> reviewStatus = createEnum("reviewStatus", mytrophy.api.game.enums.ReviewStatus.class);

    public QGameReview(String variable) {
        this(GameReview.class, forVariable(variable), INITS);
    }

    public QGameReview(Path<? extends GameReview> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGameReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGameReview(PathMetadata metadata, PathInits inits) {
        this(GameReview.class, metadata, inits);
    }

    public QGameReview(Class<? extends GameReview> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.game = inits.isInitialized("game") ? new QGame(forProperty("game")) : null;
        this.member = inits.isInitialized("member") ? new mytrophy.api.member.entity.QMember(forProperty("member")) : null;
    }

}

