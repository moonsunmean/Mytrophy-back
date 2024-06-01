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

    public final NumberPath<Integer> appId = createNumber("appId", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

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

