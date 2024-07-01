package mytrophy.global.handler;

public class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(String message) {
        super(message);
    }
}
