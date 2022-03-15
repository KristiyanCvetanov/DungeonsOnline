package bg.sofia.uni.fmi.mjt.dungeon.game.state;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.DungeonMap;
import bg.sofia.uni.fmi.mjt.dungeon.randomizer.MinionRandomizer;
import bg.sofia.uni.fmi.mjt.dungeon.randomizer.TreasureRandomizer;

import java.io.IOException;
import java.io.Reader;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private final Map<SocketAddress, Integer> addresses;
    private final Map<SocketAddress, Coordinates> coordinates;
    private final Map<Integer, Integer> playersInConflict;
    private final Map<SocketAddress, Hero> heroes;
    private final AchievementCounter achievementCounter;

    private final DungeonMap map;

    private final TreasureRandomizer treasures;
    private MinionRandomizer minions;

    public GameState(Reader mapReader, Reader treasureReader, Reader minionReader) {
        addresses = new HashMap<>();
        coordinates = new HashMap<>();
        playersInConflict = new HashMap<>();
        heroes = new HashMap<>();
        achievementCounter = new AchievementCounter();

        map = new DungeonMap(mapReader);

        treasures = TreasureRandomizer.getInstance(treasureReader);
        try {
            treasureReader.reset();
            minions = MinionRandomizer.getInstance(treasureReader, minionReader);
        } catch (IOException e) {
            loadMinionsWithDefaultTreasures(minionReader);
        }
    }

    public Map<SocketAddress, Integer> getPlayersAddresses() {
        return addresses;
    }

    public Map<SocketAddress, Coordinates> getPlayersCoordinates() {
        return coordinates;
    }

    public Map<Integer, Integer> getPlayersInConflict() {
        return playersInConflict;
    }

    public Map<SocketAddress, Hero> getHeroes() {
        return heroes;
    }

    public AchievementCounter getAchievementCounter() {
        return achievementCounter;
    }

    public DungeonMap getMap() {
        return map;
    }

    public TreasureRandomizer getTreasures() {
        return treasures;
    }

    public MinionRandomizer getMinions() {
        return minions;
    }

    private void loadMinionsWithDefaultTreasures(Reader minionReader) {
        final String defaultTreasureDir = "treasures.txt";
        try {
            Reader treasureReader = Files.newBufferedReader(Path.of(defaultTreasureDir));
            minions = MinionRandomizer.getInstance(treasureReader, minionReader);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load default treasures.\n");
        }
    }
}
