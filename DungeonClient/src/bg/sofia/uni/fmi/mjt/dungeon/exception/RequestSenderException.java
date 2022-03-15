package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class RequestSenderException extends RuntimeException {

    public RequestSenderException(String message) {
        super(message);
    }

    public RequestSenderException(String message, Exception e) {
        super(message, e);
    }
}
