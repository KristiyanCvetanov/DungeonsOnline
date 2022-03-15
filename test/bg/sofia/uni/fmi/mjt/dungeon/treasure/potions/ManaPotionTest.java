package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ManaPotionTest {

    private Drinkable potion;

    @Before
    public void setup() {
        potion = new ManaPotion("Lesser Mana Potion", 50);
    }

    @Test
    public void testGetInfo() {
        String expected = """
                    Treasure type: Mana potion
                    Name: Lesser Mana Potion
                    Mana points: 50
                """;

        String result = potion.getInfo();

        assertEquals("getInfo", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHealIfHeroIsNull() {
        potion.heal(null);
    }
}

