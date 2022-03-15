package bg.sofia.uni.fmi.mjt.dungeon.game.state;

import java.util.HashMap;
import java.util.Map;

public class AchievementCounter {

    private final Map<Integer, Integer> treasuresFound;
    private final Map<Integer, Integer> minionsKilled;
    private final Map<Integer, Integer> playersKilled;

    public AchievementCounter() {
        treasuresFound = initializeAchievementCollection();
        minionsKilled = initializeAchievementCollection();
        playersKilled = initializeAchievementCollection();
    }

    private Map<Integer, Integer> initializeAchievementCollection() {
        Map<Integer, Integer> achievementCollection = new HashMap<>();

        final int maxNumOfPlayers = 9;
        for (int i = 1; i <= maxNumOfPlayers; i++) {
            achievementCollection.put(i, 0);
        }

        return achievementCollection;
    }

    public void addToTreasuresFound(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        int currCount = treasuresFound.get(numOfPlayer);
        treasuresFound.put(numOfPlayer, currCount + 1);
    }

    public void addToMinionsKilled(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        int currCount = minionsKilled.get(numOfPlayer);
        minionsKilled.put(numOfPlayer, currCount + 1);
    }

    public void addToPlayersKilled(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        int currCount = playersKilled.get(numOfPlayer);
        playersKilled.put(numOfPlayer, currCount + 1);
    }

    public void reset(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        treasuresFound.put(numOfPlayer, 0);
        minionsKilled.put(numOfPlayer, 0);
        playersKilled.put(numOfPlayer, 0);
    }

    public int getTreasuresFound(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        return treasuresFound.get(numOfPlayer);
    }

    public int getMinionsKilled(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        return minionsKilled.get(numOfPlayer);
    }

    public int getPlayersKilled(int numOfPlayer) {
        checkNumOfPlayer(numOfPlayer);

        return playersKilled.get(numOfPlayer);
    }

    private void checkNumOfPlayer(int numOfPlayer) {
        int maxNumOfPlayers = 9;
        if (numOfPlayer <= 0 || numOfPlayer > maxNumOfPlayers) {
            throw new IllegalArgumentException("Invalid player icon given.\n");
        }
    }
}
