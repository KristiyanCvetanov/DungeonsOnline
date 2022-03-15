package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ArmorTest {

    private Hero hero;
    private Armor armor;

    @Before
    public void setup() {
        hero = new Hero("Kris");
        armor = new Armor("Knight Armor", 1, 5, 10);
    }

    @Test
    public void testUseIfHeroHasRequiredLevel() {
        hero.addToBackpack(armor);

        assertTrue("use armor with required level", armor.use(hero));
    }

    @Test
    public void testUseIfHeroLacksRequiredLevel() {
        armor = new Armor("Gold Armor", 2, 20, 40);
        hero.addToBackpack(armor);

        assertFalse("use armor without required level", armor.use(hero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUseIfHeroIsNull() {
        hero = null;

        armor.use(hero);
    }

    @Test
    public void testGetInfo() {
        String expected = """
                    Treasure type: Armor
                    Name: Knight Armor
                    Level: 1
                    Defence: 5
                    Health: 10
                """;

        String result = armor.getInfo();

        assertEquals("getInfo", expected, result);
    }

    @Test
    public void testGetPower() {
        final int healthIndex = 7;
        final int defenceIndex = 5;

        final int expected = armor.getHealth() * healthIndex
                + armor.getDefence() * defenceIndex;
        final int result = armor.getPower();

        assertEquals("getPower", expected, result);
    }
}
