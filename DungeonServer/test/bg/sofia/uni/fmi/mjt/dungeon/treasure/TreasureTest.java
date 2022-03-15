package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TreasureTest {


    @Mock
    private Hero hero;

    private static Treasure treasure;

    @BeforeClass
    public static void setup() {
        treasure = new Weapon("Sword", 1, 10);
    }


    @Test
    public void testCollectIfBackpackHasSpace() {
        when(hero.addToBackpack(treasure)).thenReturn(true);

        String expected = "Sword collected successfully in backpack." + System.lineSeparator();
        String result = treasure.collect(hero);

        assertEquals("collect if backpack has space", expected, result);
    }

    @Test
    public void testCollectIfBackpackIsFull() {
        when(hero.addToBackpack(treasure)).thenReturn(false);

        String expected = "Your backpack is full!" + System.lineSeparator();
        String result = treasure.collect(hero);

        assertEquals("collect if backpack is full", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectIfHeroIsNull() {
        hero = null;

        treasure.collect(hero);
    }
}
