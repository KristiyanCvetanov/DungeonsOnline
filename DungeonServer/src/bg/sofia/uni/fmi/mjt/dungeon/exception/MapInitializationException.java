package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class MapInitializationException extends RuntimeException {

    public MapInitializationException(String message) {
        super(message);
    }

    public MapInitializationException(String message, Exception e) {
        super(message, e);
    }
}
