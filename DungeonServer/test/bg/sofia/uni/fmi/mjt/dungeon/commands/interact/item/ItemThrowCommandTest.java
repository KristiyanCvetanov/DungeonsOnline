package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.net.SocketAddress;
import java.util.Set;


public class ItemThrowCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final int ITEM_INDEX = 2;
    private final Hero testHero = TestConfiguration.getTestHero();
    private Command itemThrowCommand;

    @Before
    public void setup() {
        itemThrowCommand = new ItemThrowCommand(TEST_ADDRESS, testHero, ITEM_INDEX);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithValidIndexShouldRemoveItem() {
        assertNotNull("hero should have that item", testHero.getItem(ITEM_INDEX));

        itemThrowCommand.execute();
        testHero.getItem(ITEM_INDEX);
    }

    @Test
    public void testExecuteWithInvalidIndexShouldNotRemoveItems() {
        final int itemIndex = 5;
        itemThrowCommand = new ItemThrowCommand(TEST_ADDRESS, testHero, itemIndex);
        itemThrowCommand.execute();

        Treasure expectedWeapon = new Weapon();
        Treasure resultWeapon = testHero.getItem(1);
        assertEquals("first item should still be in backpack", expectedWeapon, resultWeapon);

        Treasure expectedPotion = new HealingPotion("Test Potion", 30);
        Treasure resultPotion = testHero.getItem(ITEM_INDEX);
        assertEquals("second item should still be in backpack", expectedPotion, resultPotion);
    }

    @Test
    public void testExecuteCheckMessageWithValidItemIndex() {
        String expected = "Test Potion thrown away." + System.lineSeparator();

        ResponseContents response = itemThrowCommand.execute();
        String result = response.message();

        assertEquals("check message with valid item index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithInvalidItemIndex() {
        final int itemIndex = 5;
        itemThrowCommand = new ItemThrowCommand(TEST_ADDRESS, testHero, itemIndex);

        String expected = "There is no such item in your backpack." + System.lineSeparator();

        ResponseContents response = itemThrowCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid item index", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = itemThrowCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message",
                1,
                clientsToSendMessage.size());
        assertEquals("requesting client should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = itemThrowCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = itemThrowCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = itemThrowCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("map should be null", exitingPlayerAddress);
    }
}
