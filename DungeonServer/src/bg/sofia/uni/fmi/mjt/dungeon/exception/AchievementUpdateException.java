package bg.sofia.uni.fmi.mjt.dungeon.exception;

public class AchievementUpdateException extends RuntimeException {

    public AchievementUpdateException(String message) {
        super(message);
    }

    public AchievementUpdateException(String message, Exception e) {
        super(message, e);
    }
}
