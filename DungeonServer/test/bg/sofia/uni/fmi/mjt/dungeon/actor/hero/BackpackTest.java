package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class BackpackTest {

    private static final Treasure TEST_POTION = new HealingPotion("Test Potion", 10);
    private static final Treasure TEST_WEAPON = new Weapon("Test Weapon", 1, 10);

    private Backpack bag;

    @Before
    public void setup() {
        bag = new Backpack();
    }

    @Test
    public void testIsFullWhenEmpty() {
        assertFalse("isFull when empty", bag.isFull());
    }

    @Test
    public void testIsFullWithOneItemInBackpack() {
        bag.add(TEST_POTION);

        assertFalse("isFull with one item", bag.isFull());
    }

    @Test
    public void testIsFullWithTenItemsInBackpack() {
        fillBackpack();

        assertTrue("isFull with ten items", bag.isFull());
    }

    @Test
    public void testAddWhenEmpty() {
        assertTrue("add when empty success", bag.add(TEST_POTION));

        Treasure t = bag.get(1);

        assertEquals("add when empty getting the item", t, TEST_POTION);
    }

    @Test
    public void testAddWithOneItemInBackpack() {
        bag.add(TEST_POTION);

        assertTrue("add with items in backpack", bag.add(TEST_WEAPON));

        Treasure t = bag.get(2);

        assertEquals("add with items in backpack getting the item", t, TEST_WEAPON);
    }

    @Test
    public void testAddWhenFull() {
        fillBackpack();

        assertFalse("add when full", bag.add(TEST_POTION));
    }

    @Test
    public void testAddAfterRemove() {
        fillBackpack();

        bag.remove(TEST_POTION);
        bag.add(TEST_WEAPON);

        assertEquals("add after remove", TEST_WEAPON, bag.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWhenEmpty() {
        bag.get(1);
    }

    @Test
    public void testGetWhenThereIsItemAtIndex() {
        bag.add(TEST_POTION);
        Treasure t = bag.get(1);

        assertEquals("get when there is item at index", t, TEST_POTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWhenThereIsNoItemAtIndex() {
        bag.add(TEST_POTION);
        bag.add(TEST_WEAPON);
        bag.remove(TEST_POTION);

        bag.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithIndexOutOfRange() {
        fillBackpack();

        bag.get(11);
    }

    @Test(expected = InvalidItemException.class)
    public void testRemoveWhenEmpty() {
        bag.remove(TEST_POTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithItemInBackpack() {
        bag.add(TEST_POTION);
        bag.remove(TEST_POTION);

        bag.get(1);
    }

    @Test(expected = InvalidItemException.class)
    public void testRemoveWithInvalidItem() {
        bag.add(TEST_POTION);
        bag.remove(TEST_WEAPON);
    }

    @Test
    public void testGetContentInfoWhenEmpty() {
        String expected = "Backpack is empty." + System.lineSeparator();
        String result = bag.getContentInfo();

        assertEquals("getContentInfo when empty", expected, result);
    }

    @Test
    public void testGetContentInfoWithItemsInBackpack() {
        bag.add(TEST_WEAPON);
        bag.add(TEST_POTION);

        String expected = """
                Backpack contents:
                  1. Test Weapon
                  2. Test Potion
                """;

        String result = bag.getContentInfo();

        assertEquals("getContentInfo with items in backpack", expected, result);
    }

    private void fillBackpack() {
        for (int i = 0; i < 10; i++) {
            bag.add(TEST_POTION);
        }
    }
}
