package bg.sofia.uni.fmi.mjt.dungeon.storage;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.minion.Minion;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.fail;

public class MinionStorageTest {

    private static MinionStorage minions;

    @BeforeClass
    public static void setup() {
        String treasureInfo = TestConfiguration.generateTreasuresFileContent();
        String minionsInfo = TestConfiguration.generateMinionsFileContent();
        Reader treasureReader = new StringReader(treasureInfo);
        Reader minionReader = new StringReader(minionsInfo);
        minions = MinionStorage.getInstance(treasureReader, minionReader);
    }

    @Test
    public void testGetMinionsByLevelIfMinionsAreCorrectLevel() {
        for (int i = 1; i < 10; i++) {
            List<Minion> currLevelMinions = minions.getMinionsByLevel(i);

            for (Minion minion : currLevelMinions) {
                if (minion.getLevel() != i) {
                    fail();
                }
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMinionsByLevelIfCollectionIsUnmodifiable() {
        List<Minion> levelOneMinions = minions.getMinionsByLevel(1);

        levelOneMinions.remove(0);
    }
}
