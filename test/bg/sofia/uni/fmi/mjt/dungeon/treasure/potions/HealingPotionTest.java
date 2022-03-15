package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HealingPotionTest {

    private Drinkable potion;

    @Before
    public void setup() {
        potion = new HealingPotion("Lesser Healing Potion", 75);
    }

    @Test
    public void testGetInfo() {
        String expected = """
                    Treasure type: Healing potion
                    Name: Lesser Healing Potion
                    Healing points: 75
                """;

        String result = potion.getInfo();

        assertEquals("getInfo", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHealIfHeroIsNull() {
        potion.heal(null);
    }
}
