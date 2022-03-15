package bg.sofia.uni.fmi.mjt.dungeon.randomizer;

import bg.sofia.uni.fmi.mjt.dungeon.storage.TreasureStorage;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.io.Reader;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public final class TreasureRandomizer {

    private static TreasureRandomizer instance = null;

    private final TreasureStorage treasures;

    private TreasureRandomizer(Reader treasureReader) {
        treasures = TreasureStorage.getInstance(treasureReader);
    }

    public static TreasureRandomizer getInstance(Reader treasureReader) {
        if (instance == null) {
            instance = new TreasureRandomizer(treasureReader);
        }

        return instance;
    }

    public Treasure getRandomTreasure(int heroLevel) {
        final int oneAdditionalLevel = 1;
        final int twoAdditionalLevels = 2;
        final Set<Treasure> treasuresWithHeroLevel = treasures.getTreasuresByLevel(heroLevel);
        final Set<Treasure> oneLevelOverTreasures = treasures.getTreasuresByLevel(heroLevel + oneAdditionalLevel);
        final Set<Treasure> twoLevelsOverTreasures = treasures.getTreasuresByLevel(heroLevel + twoAdditionalLevels);

        final Set<Treasure> combinedTreasures = new HashSet<>(treasuresWithHeroLevel);
        combinedTreasures.addAll(oneLevelOverTreasures);
        combinedTreasures.addAll(twoLevelsOverTreasures);

        List<Treasure> treasureList = new ArrayList<>(combinedTreasures);

        Random r = new Random();
        int index = Math.abs(r.nextInt() % treasureList.size());

        return treasureList.get(index);
    }
}
