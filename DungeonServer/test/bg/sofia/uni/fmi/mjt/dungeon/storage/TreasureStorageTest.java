package bg.sofia.uni.fmi.mjt.dungeon.storage;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import static org.junit.Assert.fail;

public class TreasureStorageTest {

    private static TreasureStorage treasures;

    @BeforeClass
    public static void setup() {
        String treasureInfo = simulateTreasures();
        Reader treasureReader = new StringReader(treasureInfo);
        treasures = TreasureStorage.getInstance(treasureReader);
    }

    @Test
    public void testGetMinionsByLevelIfMinionsAreCorrectLevel() {
        for (int i = 1; i < 10; i++) {
            Set<Treasure> currLevelTreasures = treasures.getTreasuresByLevel(i);

            for (Treasure treasure : currLevelTreasures) {
                if (treasure.getLevel() != i) {
                    fail();
                }
            }
        }
    }

    private static String simulateTreasures() {
        return """
                10
                Test Weapon1, 1, 10
                Test Weapon2, 2, 20
                Test Weapon3, 3, 30
                Test Weapon4, 4, 40
                Test Weapon5, 5, 50
                Test Weapon6, 6, 60
                Test Weapon7, 7, 70
                Test Weapon8, 8, 80
                Test Weapon9, 9, 90
                Test Weapon10, 10, 100
                10
                Test Spell1, 1, 10, 10
                Test Spell2, 2, 20, 20
                Test Spell3, 3, 30, 30
                Test Spell4, 4, 40, 40
                Test Spell5, 5, 50, 50
                Test Spell6, 6, 60, 60
                Test Spell7, 7, 70, 70
                Test Spell8, 8, 80, 80
                Test Spell9, 9, 90, 90
                Test Spell10, 10, 100, 100
                10
                Test Armor1, 1, 10, 10
                Test Armor2, 2, 20, 20
                Test Armor3, 3, 30, 30
                Test Armor4, 4, 40, 40
                Test Armor5, 5, 50, 50
                Test Armor6, 6, 60, 60
                Test Armor7, 7, 70, 70
                Test Armor8, 8, 80, 80
                Test Armor9, 9, 90, 90
                Test Armor10, 10, 100, 100
                3
                Lesser Healing Potion, 75
                Medium Healing Potion, 150
                Greater Healing Potion, 300
                3
                Lesser Mana Potion, 50
                Medium Mana Potion, 100
                Greater Mana Potion, 150""";
    }
}