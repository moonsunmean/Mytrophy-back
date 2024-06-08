package mytrophy.api.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGameRead is a Querydsl query type for GameRead
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameRead extends EntityPathBase<GameRead> {

    private static final long serialVersionUID = 1631285917L;

    public static final QGameRead gameRead = new QGameRead("gameRead");

    public final mytrophy.api.common.base.QBaseEntity _super = new mytrophy.api.common.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> lastAppId = createNumber("lastAppId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QGameRead(String variable) {
        super(GameRead.class, forVariable(variable));
    }

    public QGameRead(Path<? extends GameRead> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGameRead(PathMetadata metadata) {
        super(GameRead.class, metadata);
    }

}

