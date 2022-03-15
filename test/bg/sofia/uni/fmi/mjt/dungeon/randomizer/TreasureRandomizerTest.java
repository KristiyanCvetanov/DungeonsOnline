package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class TreasureRandomizerTest {

    private static TreasureRandomizer randomizer;

    @BeforeClass
    public static void setup() {
        String treasureInfo = TestConfiguration.generateTreasuresFileContent();
        Reader treasureReader = new StringReader(treasureInfo);
        randomizer = TreasureRandomizer.getInstance(treasureReader);
    }

    @Test
    public void testGetRandomTreasureIfCorrectLevel() {
        Random r = new Random();
        final int repeatTimes = 10_000;

        for (int i = 1; i < repeatTimes; i++) {
            final int level = Math.abs(r.nextInt()) % 10 + 1;

            Treasure t = randomizer.getRandomTreasure(level);
            boolean isCorrectLevel = t.getLevel() >= level && t.getLevel() <= level + 2;

            assertTrue("getRandomTreasure if correct level", isCorrectLevel);
        }
    }
}
