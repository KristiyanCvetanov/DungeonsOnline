package bg.sofia.uni.fmi.mjt.dungeon.commands.hero;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.Set;

public class BackpackInfoCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final Hero TEST_HERO = TestConfiguration.getTestHero();
    private Command backpackInfoCommand;

    @Before
    public void setup() {
        backpackInfoCommand = new BackpackInfoCommand(TEST_ADDRESS, TEST_HERO);
    }

    @Test
    public void testExecuteCheckMessageWithItemsInBackpack() {
        String expected = """
                Backpack contents:
                  1. Bare Fists
                  2. Test Potion
                """;

        ResponseContents response = backpackInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with items in backpack", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithEmptyBackpack() {
        String expected = "Backpack is empty." + System.lineSeparator();

        backpackInfoCommand = new BackpackInfoCommand(TEST_ADDRESS, new Hero("Ivan"));
        ResponseContents response = backpackInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with empty backpack", expected, result);
    }

    @Test
    public void testExecuteShouldReturnTheSameAnswerIfRepeated() {
        backpackInfoCommand.execute();

        String expected = """
                Backpack contents:
                  1. Bare Fists
                  2. Test Potion
                """;

        ResponseContents response = backpackInfoCommand.execute();
        String result = response.message();

        assertEquals("message should be the same as before", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = backpackInfoCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());

        assertEquals("the requesting client should be send message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = backpackInfoCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = backpackInfoCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = backpackInfoCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }
}
