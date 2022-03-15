package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DamageDealingTest {

    private DamageDealing damageDealing;

    @Test
    public void testGetPowerWIth20Attack() {
        damageDealing = new Weapon("Weapon Name", 1, 20);
        final int powerIndex = 10;

        int expected = damageDealing.getDamage() * powerIndex;
        int result = damageDealing.getPower();

        assertEquals("getPower with 20 damage", expected, result);
    }

    @Test
    public void testGetPowerWIth0Attack() {
        damageDealing = new Weapon("Weapon Name", 1, 0);
        final int powerIndex = 10;

        int expected = damageDealing.getDamage() * powerIndex;
        int result = damageDealing.getPower();

        assertEquals("getPower with 0 damage", expected, result);
    }
}
