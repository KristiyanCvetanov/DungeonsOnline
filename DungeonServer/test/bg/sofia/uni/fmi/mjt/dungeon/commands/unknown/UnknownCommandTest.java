package bg.sofia.uni.fmi.mjt.dungeon.commands.unknown;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.Set;

public class UnknownCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final String TEST_MESSAGE = "test message";
    private static Command unknownCommand;

    @BeforeClass
    public static void setup() {
        unknownCommand = new UnknownCommand(TEST_ADDRESS, TEST_MESSAGE);
    }

    @Test
    public void testExecuteShouldReturnExpectedMessage() {
        ResponseContents response = unknownCommand.execute();
        String result = response.message();

        assertEquals("execute should return expected message", TEST_MESSAGE, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = unknownCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should get message", 1, clientsToSendMessage.size());
        assertEquals("requesting client should get message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = unknownCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should get map", 0, clientsToSendMap.size());
        assertNull("map should be null", response.updatedMap());
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = unknownCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("no one should exit game", exitingPlayerAddress);
    }
}
