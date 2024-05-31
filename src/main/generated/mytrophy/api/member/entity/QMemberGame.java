package mytrophy.api.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberGame is a Querydsl query type for MemberGame
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberGame extends EntityPathBase<MemberGame> {

    private static final long serialVersionUID = 174569865L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberGame memberGame = new QMemberGame("memberGame");

    public final NumberPath<Long> appId = createNumber("appId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final NumberPath<Integer> playtimeForever = createNumber("playtimeForever", Integer.class);

    public QMemberGame(String variable) {
        this(MemberGame.class, forVariable(variable), INITS);
    }

    public QMemberGame(Path<? extends MemberGame> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberGame(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberGame(PathMetadata metadata, PathInits inits) {
        this(MemberGame.class, metadata, inits);
    }

    public QMemberGame(Class<? extends MemberGame> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

