package bg.sofia.uni.fmi.mjt.dungeon.commands;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.help.HelpCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.Test;

import java.net.SocketAddress;

import static org.junit.Assert.assertEquals;

public class CommandExecutorTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private CommandExecutor executor;

    @Test
    public void testExecuteCommand() {
        Command helpCommand = new HelpCommand(TEST_ADDRESS);
        executor = new CommandExecutor(helpCommand);
        ResponseContents response = executor.executeCommand();

        assertEquals("only one person should get the message",
                1,
                response.clientsToSendMessage().size());

        SocketAddress addrResponse = response.clientsToSendMessage().iterator().next();
        assertEquals("the player should be the asking one", TEST_ADDRESS, addrResponse);
    }
}
