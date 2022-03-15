package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class InvalidItemException extends RuntimeException {

    public InvalidItemException(String message) {
        super(message);
    }

    public InvalidItemException(String message, RuntimeException e) {
        super(message, e);
    }
}
