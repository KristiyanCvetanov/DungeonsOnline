package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;

import java.util.Random;

public class MapFreeSpotRandomizer {

    private final char[][] map;
    private final int mapWidth;
    private final int mapLength;

    public MapFreeSpotRandomizer(char[][] map) {
        this.map = map;
        mapWidth = map.length;
        mapLength = map[0].length;
    }

    public Coordinates getRandomFreeSpot() {
        Random r = new Random();

        int x = Math.abs(r.nextInt() % mapLength);
        int y = Math.abs(r.nextInt() % mapWidth);
        char spot = map[y][x];

        while (spot != '.') {
            x = Math.abs(r.nextInt() % mapLength);
            y = Math.abs(r.nextInt() % mapWidth);
            spot = map[y][x];
        }

        return new Coordinates(x, y);
    }
}
