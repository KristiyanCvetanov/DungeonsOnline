package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class WeaponTest {

    private Hero hero;
    private Weapon weapon;

    @Before
    public void setup() {
        hero = new Hero("Kris");
        weapon = new Weapon("Thief Knife", 1, 10);
    }

    @Test
    public void testUseIfHeroHasRequiredLevel() {
        hero.addToBackpack(weapon);

        assertTrue("use weapon with required level", weapon.use(hero));
    }

    @Test
    public void testUseIfHeroLacksRequiredLevel() {
        weapon = new Weapon("Knight Sword", 2, 20);
        hero.addToBackpack(weapon);

        assertFalse("use weapon without required level", weapon.use(hero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUseIfHeroIsNull() {
        hero = null;

        weapon.use(hero);
    }

    @Test
    public void testGetInfo() {
        String expected = """
                    Treasure type: Weapon
                    Name: Thief Knife
                    Level: 1
                    Damage: 10
                """;

        String result = weapon.getInfo();

        assertEquals("getInfo", expected, result);
    }
}
