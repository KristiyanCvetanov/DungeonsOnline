package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class DungeonClientException extends RuntimeException {

    public DungeonClientException(String message) {
        super(message);
    }

    public DungeonClientException(String message, Exception e) {
        super(message, e);
    }
}
