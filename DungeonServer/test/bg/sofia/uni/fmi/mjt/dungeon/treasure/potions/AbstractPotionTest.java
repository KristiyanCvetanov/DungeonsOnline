package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AbstractPotionTest {

    private AbstractPotion potion;

    @Before
    public void setup() {
        potion = new HealingPotion("Abstract Potion", 0);
    }

    @Test
    public void testUseShouldAlwaysBeSuccessful() {
        Hero hero = new Hero("Ivan");
        potion.collect(hero);

        assertTrue("use should always be successful", potion.use(hero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUseIfHeroIsNull() {
        Hero hero = null;

        potion.use(hero);
    }
}
