package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class ServerConfigurationException extends RuntimeException {

    public ServerConfigurationException(String message) {
        super(message);
    }

    public ServerConfigurationException(String message, Exception e) {
        super(message, e);
    }
}
