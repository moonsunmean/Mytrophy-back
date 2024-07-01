package mytrophy.api.game.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTopGameRead is a Querydsl query type for TopGameRead
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopGameRead extends EntityPathBase<TopGameRead> {

    private static final long serialVersionUID = -1470107896L;

    public static final QTopGameRead topGameRead = new QTopGameRead("topGameRead");

    public final NumberPath<Integer> AppId = createNumber("AppId", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QTopGameRead(String variable) {
        super(TopGameRead.class, forVariable(variable));
    }

    public QTopGameRead(Path<? extends TopGameRead> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTopGameRead(PathMetadata metadata) {
        super(TopGameRead.class, metadata);
    }

}

