package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class ServerStoppingException extends RuntimeException {

    public ServerStoppingException(String message) {
        super(message);
    }

    public ServerStoppingException(String message, Exception e) {
        super(message, e);
    }
}
