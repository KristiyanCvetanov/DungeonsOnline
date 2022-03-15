package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.actor.minion.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.storage.MinionStorage;

import java.io.Reader;
import java.util.List;
import java.util.Random;

public final class MinionRandomizer {

    private static MinionRandomizer instance = null;

    private final MinionStorage minions;

    private MinionRandomizer(Reader treasureReader, Reader minionsReader) {
        minions = MinionStorage.getInstance(treasureReader, minionsReader);
    }

    public static MinionRandomizer getInstance(Reader treasureReader, Reader minionsReader) {
        if (instance == null) {
            instance = new MinionRandomizer(treasureReader, minionsReader);
        }

        return instance;
    }

    public Minion getRandomMinion(int heroLevel) {
        List<Minion> minionsWithHeroLevel = minions.getMinionsByLevel(heroLevel);

        Random r = new Random();
        int index = Math.abs(r.nextInt() % minionsWithHeroLevel.size());

        return minionsWithHeroLevel.get(index);
    }
}
