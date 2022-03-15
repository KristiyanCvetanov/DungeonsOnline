package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class ResponseListenerException extends RuntimeException {

    public ResponseListenerException(String message) {
        super(message);
    }

    public ResponseListenerException(String message, Exception e) {
        super(message, e);
    }
}
