package bg.sofia.uni.fmi.mjt.dungeon.commands.help;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.SocketAddress;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HelpCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static Command helpCommand;


    @BeforeClass
    public static void setup() {
        helpCommand = new HelpCommand(TEST_ADDRESS);
    }

    @Test
    public void testExecuteIfReturnsExpectedMessage() {
        ResponseContents response = helpCommand.execute();

        String expected = TestConfiguration.getCommandsInfo();
        String result = response.message();

        assertEquals("execute check message", expected, result);
    }

    @Test
    public void testExecuteMessageShouldBeSentToAskingClient() {
        ResponseContents response = helpCommand.execute();

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("there should be only 1 client", 1, clientsToSendMessage.size());

        SocketAddress addrResponse = response.clientsToSendMessage().iterator().next();
        assertEquals("message to be sent to right client", TEST_ADDRESS, addrResponse);
    }

    @Test
    public void testExecuteMapShouldNotBeSentToAnyone() {
        ResponseContents response = helpCommand.execute();

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("there should be 0 clients", 0, clientsToSendMap.size());

        assertNull("map should be null", response.updatedMap());
    }

    @Test
    public void testExecuteShouldMakeNoPlayerExitTheGame() {
        ResponseContents response = helpCommand.execute();

        assertNull("exiting player should be null", response.exitingPlayerAddress());
    }
}
