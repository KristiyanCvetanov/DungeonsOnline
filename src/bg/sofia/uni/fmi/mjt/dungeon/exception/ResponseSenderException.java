package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class ResponseSenderException extends RuntimeException {

    public ResponseSenderException(String message) {
        super(message);
    }

    public ResponseSenderException(String message, Exception e) {
        super(message, e);
    }
}
