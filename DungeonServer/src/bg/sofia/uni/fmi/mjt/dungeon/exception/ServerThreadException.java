package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class ServerThreadException extends RuntimeException {

    public ServerThreadException(String message) {
        super(message);
    }

    public ServerThreadException(String message, Exception e) {
        super(message, e);
    }
}
