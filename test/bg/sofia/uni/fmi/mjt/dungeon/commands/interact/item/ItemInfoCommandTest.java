package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.Set;

public class ItemInfoCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final Hero TEST_HERO = TestConfiguration.getTestHero();
    private static Command itemInfoCommand;

    @BeforeClass
    public static void setup() {
        final int itemIndex = 2;
        itemInfoCommand = new ItemInfoCommand(TEST_ADDRESS, TEST_HERO, itemIndex);
    }

    @Test
    public void testExecuteCheckMessageWithValidItemIndex() {
        String expected = """
                    Treasure type: Healing potion
                    Name: Test Potion
                    Healing points: 30
                """;
        ResponseContents response = itemInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with valid item index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithInvalidItemIndex() {
        final int itemIndex = -5;
        itemInfoCommand = new ItemInfoCommand(TEST_ADDRESS, TEST_HERO, itemIndex);

        String expected = "There is no such item in your backpack." + System.lineSeparator();
        ResponseContents response = itemInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid item index", expected, result);

        // return static variable to old state
        final int oldItemIndex = 2;
        itemInfoCommand = new ItemInfoCommand(TEST_ADDRESS, TEST_HERO, oldItemIndex);
    }

    @Test
    public void testExecuteShouldReturnTheSameMessageIfRepeated() {
        itemInfoCommand.execute();
        itemInfoCommand.execute();
        String expected = """
                    Treasure type: Healing potion
                    Name: Test Potion
                    Healing points: 30
                """;
        ResponseContents response = itemInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with valid item index", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = itemInfoCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());
        assertEquals("requesting client should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = itemInfoCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = itemInfoCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = itemInfoCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }
}
