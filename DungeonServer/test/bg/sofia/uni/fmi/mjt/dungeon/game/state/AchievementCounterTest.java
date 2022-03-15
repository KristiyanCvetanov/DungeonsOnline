package bg.sofia.uni.fmi.mjt.dungeon.game.state;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AchievementCounterTest {

    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;
    private AchievementCounter achievements;

    @Before
    public void setup() {
        achievements = new AchievementCounter();
    }

    @Test
    public void testAddToTreasuresFoundWithValidArgument() {
        achievements.addToTreasuresFound(PLAYER_1);
        achievements.addToTreasuresFound(PLAYER_1);

        achievements.addToTreasuresFound(PLAYER_2);

        final int expected1 = 2;
        final int actual1 = achievements.getTreasuresFound(PLAYER_1);
        assertEquals("first player should have 2 treasures found", expected1, actual1);

        final int expected2 = 1;
        final int actual2 = achievements.getTreasuresFound(PLAYER_2);
        assertEquals("second player should have 1 treasure found", expected2, actual2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToTreasuresFoundWithInvalidArgument() {
        final int invalidPlayerIcon = 0;
        achievements.addToTreasuresFound(invalidPlayerIcon);
    }

    @Test
    public void testAddToMinionsKilledWithValidArgument() {
        achievements.addToMinionsKilled(PLAYER_1);

        achievements.addToMinionsKilled(PLAYER_2);
        achievements.addToMinionsKilled(PLAYER_2);
        achievements.addToMinionsKilled(PLAYER_2);

        final int expected1 = 1;
        final int actual1 = achievements.getMinionsKilled(PLAYER_1);
        assertEquals("first player should have 1 minion killed", expected1, actual1);

        final int expected2 = 3;
        final int actual2 = achievements.getMinionsKilled(PLAYER_2);
        assertEquals("second player should have 3 minions killed", expected2, actual2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToMinionsKilledWithInvalidArgument() {
        final int invalidPlayerIcon = -2;
        achievements.addToMinionsKilled(invalidPlayerIcon);
    }

    @Test
    public void testAddToPlayersKilledWithValidArgument() {
        achievements.addToPlayersKilled(PLAYER_1);
        achievements.addToPlayersKilled(PLAYER_1);

        achievements.addToPlayersKilled(PLAYER_2);
        achievements.addToPlayersKilled(PLAYER_2);

        final int expected1 = 2;
        final int actual1 = achievements.getPlayersKilled(PLAYER_1);
        assertEquals("first player should have 2 players killed", expected1, actual1);

        final int expected2 = 2;
        final int actual2 = achievements.getPlayersKilled(PLAYER_2);
        assertEquals("second player should have 2 players killed", expected2, actual2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToPlayersKilledWithInvalidArgument() {
        final int invalidPlayerIcon = 10;
        achievements.addToPlayersKilled(invalidPlayerIcon);
    }

    @Test
    public void testResetWithValidArgument() {
        achievements.addToTreasuresFound(PLAYER_1);
        achievements.addToMinionsKilled(PLAYER_1);
        achievements.addToPlayersKilled(PLAYER_1);
        achievements.addToPlayersKilled(PLAYER_1);

        achievements.addToTreasuresFound(PLAYER_2);
        achievements.addToMinionsKilled(PLAYER_2);
        achievements.addToMinionsKilled(PLAYER_2);
        achievements.addToPlayersKilled(PLAYER_2);

        achievements.reset(PLAYER_1);
        achievements.reset(PLAYER_2);

        final int expected1 = 0;
        final int actualTreasures1 = achievements.getTreasuresFound(PLAYER_1);
        final int actualMinions1 = achievements.getMinionsKilled(PLAYER_1);
        final int actualPlayers1 = achievements.getPlayersKilled(PLAYER_1);

        assertEquals("first player treasures should be 0 after reset", expected1, actualTreasures1);
        assertEquals("first player minions should be 0 after reset", expected1, actualMinions1);
        assertEquals("first player players should be 0 after reset", expected1, actualPlayers1);

        final int expected2 = 0;
        final int actualTreasures2 = achievements.getTreasuresFound(PLAYER_2);
        final int actualMinions2 = achievements.getMinionsKilled(PLAYER_2);
        final int actualPlayers2 = achievements.getPlayersKilled(PLAYER_2);

        assertEquals("second player treasures should be 0 after reset", expected2, actualTreasures2);
        assertEquals("second player minions should be 0 after reset", expected2, actualMinions2);
        assertEquals("second player players should be 0 after reset", expected2, actualPlayers2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetWithInvalidArgument() {
        final int invalidPlayerIcon = 17;
        achievements.reset(invalidPlayerIcon);
    }
}
