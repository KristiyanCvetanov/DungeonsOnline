package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.minion.Minion;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MinionRandomizerTest {

    private MinionRandomizer randomizer;

    @Before
    public void setup() {
        String treasureInfo = TestConfiguration.generateTreasuresFileContent();
        String minionsInfo = TestConfiguration.generateMinionsFileContent();
        Reader treasureReader = new StringReader(treasureInfo);
        Reader minionReader = new StringReader(minionsInfo);
        randomizer = MinionRandomizer.getInstance(treasureReader, minionReader);
    }

    @Test
    public void testGetRandomTreasureIfCorrectLevel() {
        Random r = new Random();
        final int repeatTimes = 10_000;

        for (int i = 1; i < repeatTimes; i++) {
            final int level = Math.abs(r.nextInt()) % 10 + 1;

            Minion m = randomizer.getRandomMinion(level);

            assertEquals("getRandomMinion if correct level", level, m.getLevel());
        }
    }
}