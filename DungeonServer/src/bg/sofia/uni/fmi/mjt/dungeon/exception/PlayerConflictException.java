package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class PlayerConflictException extends RuntimeException {

    public PlayerConflictException(String message) {
        super(message);
    }

    public PlayerConflictException(String message, Exception e) {
        super(message, e);
    }
}
