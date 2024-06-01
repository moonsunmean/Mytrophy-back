package mytrophy.api.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGameData is a Querydsl query type for GameData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameData extends EntityPathBase<GameData> {

    private static final long serialVersionUID = 1630865585L;

    public static final QGameData gameData = new QGameData("gameData");

<<<<<<< HEAD
    public final mytrophy.api.common.base.QBaseEntity _super = new mytrophy.api.common.base.QBaseEntity(this);

    public final NumberPath<Integer> appId = createNumber("appId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

=======
    public final NumberPath<Integer> appId = createNumber("appId", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

>>>>>>> cf4026e880bd58e569949d77a0e2c22366fb8c1b
    public QGameData(String variable) {
        super(GameData.class, forVariable(variable));
    }

    public QGameData(Path<? extends GameData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGameData(PathMetadata metadata) {
        super(GameData.class, metadata);
    }

}

