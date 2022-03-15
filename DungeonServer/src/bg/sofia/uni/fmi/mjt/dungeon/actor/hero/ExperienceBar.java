package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class ExperienceBar {

    private static final int EXPERIENCE_CAP = 15_000;
    private static ExperienceBar instance = null;

    private final NavigableMap<Integer, Integer> sectors;
    private final Map<Integer, Integer> levels;

    private ExperienceBar() {
        sectors = initializeSectors();
        levels = initializeLevels();
    }

    public static ExperienceBar getInstance() {
        if (instance == null) {
            instance = new ExperienceBar();
        }

        return instance;
    }

    public static int getExperienceCap() {
        return EXPERIENCE_CAP;
    }

    public int levelUp(int currExperience) {
        if (currExperience < 0) {
            throw new IllegalArgumentException("Experience given in levelUp method is negative.\n");
        }

        if (currExperience >= EXPERIENCE_CAP) {
            int levelCap = 10;
            return levelCap;
        }

        int floorKey = sectors.floorKey(currExperience);
        return levels.get(floorKey);
    }

    public int neededXpForNextLevel(int currExperience) {
        if (currExperience < 0) {
            throw new IllegalArgumentException("Experience given in neededXpForNextLevel method is negative.\n");
        }

        if (currExperience >= EXPERIENCE_CAP) {
            return 0;
        } else if (sectors.containsKey(currExperience)) {
            return sectors.get(currExperience) - currExperience + 1;
        }

        int xpForNextLevel = sectors.ceilingKey(currExperience);

        return xpForNextLevel - currExperience;
    }

    private static NavigableMap<Integer, Integer> initializeSectors() {
        Map<Integer, Integer> mapSectors = Map.ofEntries(
                Map.entry(0, 999),
                Map.entry(1000, 2199),
                Map.entry(2200, 3499),
                Map.entry(3500, 4999),
                Map.entry(5000, 6799),
                Map.entry(6800, 8499),
                Map.entry(8500, 10499),
                Map.entry(10500, 12699),
                Map.entry(12700, 14999),
                Map.entry(15000, 15000)
        );

        return new TreeMap<>(mapSectors);
    }

    private static Map<Integer, Integer> initializeLevels() {
        return Map.ofEntries(
                Map.entry(0, 1),
                Map.entry(1000, 2),
                Map.entry(2200, 3),
                Map.entry(3500, 4),
                Map.entry(5000, 5),
                Map.entry(6800, 6),
                Map.entry(8500, 7),
                Map.entry(10500, 8),
                Map.entry(12700, 9),
                Map.entry(15000, 10)
        );
    }
}
