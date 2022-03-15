package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SpellTest {

    private Hero hero;
    private Spell spell;

    @Before
    public void setup() {
        hero = new Hero("Kris");
        spell = new Spell("Moonfire", 1, 10, 50);
    }

    @Test
    public void testUseIfHeroHasRequiredLevel() {
        hero.addToBackpack(spell);

        assertTrue("use spell with required level", spell.use(hero));
    }

    @Test
    public void testUseIfHeroLacksRequiredLevel() {
        spell = new Spell("Fireball", 2, 20, 100);
        hero.addToBackpack(spell);

        assertFalse("use spell without required level", spell.use(hero));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUseIfHeroIsNull() {
        hero = null;

        spell.use(hero);
    }

    @Test
    public void testGetInfo() {
        String expected = """
                    Treasure type: Spell
                    Name: Moonfire
                    Level: 1
                    Damage: 10
                    Mana cost: 50
                """;

        String result = spell.getInfo();

        assertEquals("getInfo", expected, result);
    }
}
