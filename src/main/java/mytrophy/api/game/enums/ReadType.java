package mytrophy.api.game.enums;

public enum ReadType {
    ONE(1),
    MANY(2),
    TOP(3);

    private final int type;

    ReadType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}

