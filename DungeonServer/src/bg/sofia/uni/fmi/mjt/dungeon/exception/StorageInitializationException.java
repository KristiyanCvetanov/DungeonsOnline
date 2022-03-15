package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class StorageInitializationException extends RuntimeException {

    public StorageInitializationException(String message) {
        super(message);
    }

    public StorageInitializationException(String message, Exception e) {
        super(message, e);
    }
}
