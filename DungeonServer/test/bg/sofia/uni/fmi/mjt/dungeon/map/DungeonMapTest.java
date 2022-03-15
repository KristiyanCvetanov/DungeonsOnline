package bg.sofia.uni.fmi.mjt.dungeon.map;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

public class DungeonMapTest {

    private DungeonMap dungeonMap;
    private char[][] map;

    @Before
    public void setup() {
        String generatedMap = TestConfiguration.generateEmptyMap();
        Reader mapReader = new StringReader(generatedMap);
        dungeonMap = new DungeonMap(mapReader);
        map = dungeonMap.getContent();
    }

    @Test
    public void testAddPlayerIfAddsPlayerOnFreeSpot() {
        Set<Coordinates> freeSpots = TestConfiguration.getFreeSpotsOnMap(map);

        Coordinates playerCoordinates = dungeonMap.addPlayer(1);

        assertEquals("addPlayer should return correct coordinates",
                '1',
                map[playerCoordinates.y()][playerCoordinates.x()]);
        assertTrue("addPlayer should put player on a free spot", freeSpots.contains(playerCoordinates));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPLayerWithNegativePlayerNumber() {
        dungeonMap.addPlayer(-2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPLayerWithBiggerPlayerNumber() {
        dungeonMap.addPlayer(10);
    }

    @Test
    public void testMovePlayerIfChangesMapCorrectly() {
        Coordinates oldCoords = dungeonMap.addPlayer(1);
        Coordinates newCoords = findValidCoords(oldCoords);

        dungeonMap.movePlayer(oldCoords, newCoords);

        assertEquals("movePlayer should move player to new location",
                '1',
                map[newCoords.y()][newCoords.x()]);
        assertEquals("movePlayer should make a free spot on old location",
                '.',
                map[oldCoords.y()][oldCoords.x()]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMovePlayerWithInvalidCoordinates() {
        Coordinates oldCoords = new Coordinates(0, 0);
        Coordinates newCoords = new Coordinates(-1, 2);

        dungeonMap.movePlayer(oldCoords, newCoords);
    }

    @Test
    public void testMarkVictoriousFromBattleShouldChangeMapCorrectly() {
        Coordinates coord = new Coordinates(0, 1);
        dungeonMap.markVictorious(coord, 'M');

        assertEquals("", 'M', map[coord.y()][coord.x()]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMarkVictoriousFromBattleWithInvalidCoordinates() {
        Coordinates coord = new Coordinates(2, 10);
        dungeonMap.markVictorious(coord, 'M');
    }

    @Test
    public void testEnterConflictShouldChangeMapCorrectly() {
        Coordinates oldCoords = dungeonMap.addPlayer(1);
        Coordinates newCoords = findValidCoords(oldCoords);

        dungeonMap.enterConflict(oldCoords, newCoords);

        assertEquals("enterConflict should put 'C' on new location",
                'C',
                map[newCoords.y()][newCoords.x()]);
        assertEquals("enterConflict should put '.' on old location",
                '.',
                map[oldCoords.y()][oldCoords.x()]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEnterConflictWithInvalidCoordinates() {
        Coordinates oldCoords = new Coordinates(0, 1);
        Coordinates newCoords = new Coordinates(20, 30);

        dungeonMap.enterConflict(oldCoords, newCoords);
    }

    @Test
    public void testExitConflictShouldChangeMapCorrectly() {
        Coordinates oldCoords = new Coordinates(1, 1);
        Coordinates newCoords = new Coordinates(2, 1);

        dungeonMap.exitConflict(oldCoords, newCoords, '1', '2');

        assertEquals("exit conflict static player should be on old location",
                '2',
                map[oldCoords.y()][oldCoords.x()]);
        assertEquals("exit conflict moving player should be on new location",
                '1',
                map[newCoords.y()][newCoords.x()]);
    }

    @Test
    public void testGetLocationsObjectFreeSpot() {
        Coordinates coords = new Coordinates(0, 0);

        assertEquals("getLocationsObject free spot",
                MapObject.FREE_SPOT,
                dungeonMap.getLocationsObject(coords));
    }

    @Test
    public void testGetLocationsObjectObstacle() {
        Coordinates coords = new Coordinates(1, 0);

        assertEquals("getLocationsObject obstacle",
                MapObject.OBSTACLE,
                dungeonMap.getLocationsObject(coords));
    }

    @Test
    public void testGetLocationsObjectTreasure() {
        Coordinates coords = new Coordinates(1, 1);
        dungeonMap.markVictorious(coords, 'T');

        assertEquals("getLocationsObject treasure",
                MapObject.TREASURE,
                dungeonMap.getLocationsObject(coords));
    }

    @Test
    public void testGetLocationsObjectMinion() {
        Coordinates coords = new Coordinates(2, 2);
        dungeonMap.markVictorious(coords, 'M');

        assertEquals("getLocationsObject minion",
                MapObject.MINION,
                dungeonMap.getLocationsObject(coords));
    }

    @Test
    public void testGetLocationsObjectPlayer() {
        Coordinates coords = dungeonMap.addPlayer(3);

        assertEquals("getLocationsObject player",
                MapObject.PLAYER,
                dungeonMap.getLocationsObject(coords));
    }

    @Test
    public void testGetLocationsObjectConflict() {
        Coordinates coords = new Coordinates(3, 3);
        dungeonMap.enterConflict(new Coordinates(3, 2), coords);

        assertEquals("getLocationsObject conflict",
                MapObject.CONFLICT,
                dungeonMap.getLocationsObject(coords));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetLocationsObjectWithInvalidCoordinates() {
        dungeonMap.getLocationsObject(new Coordinates(-2, -7));
    }

    private Coordinates findValidCoords(Coordinates oldCoords) {
        char[][] map = dungeonMap.getContent();
        final int x = oldCoords.x();
        final int y = oldCoords.y();

        if (x + 1 >= 0 && x + 1 < map[0].length && map[y][x + 1] != '#') {
            return new Coordinates(x + 1, y);
        } else if (x - 1 >= 0 && x - 1 < map[0].length && map[y][x - 1] != '#') {
            return new Coordinates(x - 1, y);
        } else if (y + 1 >= 0 && x + 1 < map[0].length && map[y][x + 1] != '#') {
            return new Coordinates(x, y + 1);
        } else {
            return new Coordinates(x, y - 1);
        }
    }
}
