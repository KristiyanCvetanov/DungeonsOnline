package bg.sofia.uni.fmi.mjt.dungeon;

import bg.sofia.uni.fmi.mjt.dungeon.communication.DungeonServer;

public class Main {
    public static void main(String[] args) {
        final String mapDirectory = "map15x15.txt";
        DungeonServer server = new DungeonServer(7777, mapDirectory);
        server.start();
        // server.stop();
    }
}
