package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapFreeSpotRandomizerTest {

    private char[][] map;
    private MapFreeSpotRandomizer randomizer;

    @Before
    public void setup() {
        map = new char[][]{
                {'.', '.', '.', '#', 'T'},
                {'T', '#', '1', '#', 'T'},
                {'.', '#', '.', '.', '.'},
                {'M', '#', '#', '.', '.'},
                {'#', '.', '.', '.', 'M'}
        };

        randomizer = new MapFreeSpotRandomizer(map);
    }

    @Test
    public void testGetRandomFreeSpotShouldReturnCorrectCoordinates() {
        final int repeatTimes = 10_000;

        for (int i = 0; i < repeatTimes; i++) {
            Coordinates coordinates = randomizer.getRandomFreeSpot();
            boolean inHorizontalBounds = (coordinates.x() >= 0 && coordinates.x() < map[0].length);
            boolean inVerticalBounds = (coordinates.y() >= 0 && coordinates.y() < map.length);

            assertTrue("getRandomFreeSpot should be in map bounds",
                    inHorizontalBounds && inVerticalBounds);

            assertEquals("getRandomFreeSpot should get a free spot",
                    '.', map[coordinates.y()][coordinates.x()]);
        }
    }
}
