package bg.sofia.uni.fmi.mjt.dungeon.map;

import bg.sofia.uni.fmi.mjt.dungeon.exception.MapInitializationException;
import bg.sofia.uni.fmi.mjt.dungeon.randomizer.MapFreeSpotRandomizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class DungeonMap {

    private char[][] map;
    private int width;
    private int height;

    public DungeonMap(Reader mapReader) {
        initializeMap(mapReader);
    }

    public char[][] getContent() {
        return map;
    }

    public Coordinates addPlayer(int numOfPlayer) {
        int maxNumOfPlayers = 9;
        if (numOfPlayer <= 0 || numOfPlayer > maxNumOfPlayers) {
            throw new IllegalArgumentException("Invalid number of player given in method addPlayer.\n");
        }

        final int radix = 10;
        char playerIcon = Character.forDigit(numOfPlayer, radix);

        var freeSpotRandomizer = new MapFreeSpotRandomizer(map);
        Coordinates freeSpot = freeSpotRandomizer.getRandomFreeSpot();
        map[freeSpot.y()][freeSpot.x()] = playerIcon;

        return freeSpot;
    }

    public void movePlayer(Coordinates oldCoordinates,
                           Coordinates newCoordinates) {
        final int oldX = oldCoordinates.x();
        final int oldY = oldCoordinates.y();
        final int newX = newCoordinates.x();
        final int newY = newCoordinates.y();

        if (areInvalidCoordinates(oldCoordinates) || areInvalidCoordinates(newCoordinates)) {
            throw new IndexOutOfBoundsException("Invalid coordinates given in movePlayer method.\n");
        }

        char playerIcon = map[oldY][oldX];
        map[oldY][oldX] = '.';
        map[newY][newX] = playerIcon;
    }

    public void markVictorious(Coordinates coordinates, char actorIcon) {
        if (areInvalidCoordinates(coordinates)) {
            throw new IndexOutOfBoundsException("Invalid coordinates given in mark victorious method.\n");
        }

        final int x = coordinates.x();
        final int y = coordinates.y();

        map[y][x] = actorIcon;
    }

    public void enterConflict(Coordinates oldCoordinates, Coordinates newCoordinates) {
        if (areInvalidCoordinates(oldCoordinates) || areInvalidCoordinates(newCoordinates)) {
            throw new IndexOutOfBoundsException("Invalid coordinates given in enterConflict method.\n");
        }

        map[oldCoordinates.y()][oldCoordinates.x()] = '.';
        map[newCoordinates.y()][newCoordinates.x()] = 'C';
    }

    public void exitConflict(Coordinates oldCoordinates,
                             Coordinates newCoordinates,
                             char movingPlayerIcon,
                             char staticPlayerIcon) {
        if (areInvalidCoordinates(oldCoordinates) || areInvalidCoordinates(newCoordinates)) {
            throw new IndexOutOfBoundsException("Invalid coordinates given in exitConflict method.\n");
        }

        map[oldCoordinates.y()][oldCoordinates.x()] = staticPlayerIcon;
        map[newCoordinates.y()][newCoordinates.x()] = movingPlayerIcon;
    }

    public MapObject getLocationsObject(Coordinates coordinates) {
        if (areInvalidCoordinates(coordinates)) {
            throw new IndexOutOfBoundsException("You are cannot surpass the borders of the dungeon!"
                    + System.lineSeparator());
        }

        char nextLocation = map[coordinates.y()][coordinates.x()];

        return switch (nextLocation) {
            case '.' -> MapObject.FREE_SPOT;
            case '#' -> MapObject.OBSTACLE;
            case 'T' -> MapObject.TREASURE;
            case 'M' -> MapObject.MINION;
            case 'C' -> MapObject.CONFLICT;
            default -> MapObject.PLAYER;
        };
    }

    public void spawnTreasure() {
        putObjectOnRandomLocation('T');
    }

    public void spawnMinion() {
        putObjectOnRandomLocation('M');
    }

    private void initializeMap(Reader mapReader) {
        try {
            var buffReader = new BufferedReader(mapReader);
            width = Integer.parseInt(buffReader.readLine());  // first two lines of file are the dimensions of the map
            height = Integer.parseInt(buffReader.readLine());

            final int numberOfTreasures = Integer.parseInt(buffReader.readLine());  // next two lines are the number of
            final int numberOfMinions = Integer.parseInt(buffReader.readLine());    // treasures and minions to spawn

            map = loadMap(buffReader);

            putObjectsOnMap('T', numberOfTreasures);
            putObjectsOnMap('M', numberOfMinions);
        } catch (IOException e) {
            throw new MapInitializationException("There is a problem with loading the map from file.\n", e);
        }
    }

    private char[][] loadMap(BufferedReader bufferReader) throws IOException {
        char[][] mapToBuild = new char[height][width];

        for (int i = 0; i < height; i++) {
            String line = bufferReader.readLine();
            mapToBuild[i] = line.toCharArray();
        }

        return mapToBuild;
    }


    private void putObjectOnRandomLocation(char object) {
        var freeSpotRandomizer = new MapFreeSpotRandomizer(map);
        Coordinates freeSpot = freeSpotRandomizer.getRandomFreeSpot();

        map[freeSpot.y()][freeSpot.x()] = object;
    }

    private void putObjectsOnMap(char object, int numOfObjects) {
        for (int i = 0; i < numOfObjects; i++) {
            putObjectOnRandomLocation(object);
        }
    }

    private boolean areInvalidCoordinates(Coordinates coordinates) {
        int mapHeight = map.length;
        int mapWidth = map[0].length;

        return coordinates.y() < 0 || coordinates.y() >= mapHeight
                || coordinates.x() < 0 || coordinates.x() >= mapWidth;
    }
}
