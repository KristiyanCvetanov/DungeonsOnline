package bg.sofia.uni.fmi.mjt.dungeon.commands.register;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import org.junit.Before;
import org.junit.Test;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class RegisterCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private GameState game;
    private Command registerCommand;

    @Before
    public void setup() {
        String mapStr = TestConfiguration.generateEmptyMap();
        game = TestConfiguration.generateGame(mapStr);
        registerCommand = new RegisterCommand(TEST_ADDRESS, game);
    }

    @Test
    public void testExecuteShouldRegisterPlayerInSystem() {
        Map<SocketAddress, Integer> players = game.getPlayersAddresses();

        assertFalse("player shouldn't be registered yet", players.containsKey(TEST_ADDRESS));

        registerCommand.execute();
        assertTrue("player should be registered after execute", players.containsKey(TEST_ADDRESS));
    }

    @Test
    public void testExecuteShouldGivePlayerExpectedIcon() {
        final List<SocketAddress> addresses = TestConfiguration.getMultipleSocketAddresses();

        Command newRegCommand = new RegisterCommand(addresses.get(0), game);
        newRegCommand.execute();  // add one player beforehand

        registerCommand.execute();

        Map<SocketAddress, Integer> players = game.getPlayersAddresses();
        final int expectedPlayerIcon = 2;
        final int actualPlayerIcon = players.get(TEST_ADDRESS);
        assertEquals("player should have expected icon", expectedPlayerIcon, actualPlayerIcon);
    }

    @Test
    public void testExecuteShouldPutPlayerOnFreeSpot() {
        Set<Coordinates> freeSpots = TestConfiguration.getFreeSpotsOnMap(game.getMap().getContent());

        registerCommand.execute();
        Coordinates playerCoordinates = game.getPlayersCoordinates().get(TEST_ADDRESS);

        assertTrue("execute should put player on free spot", freeSpots.contains(playerCoordinates));
    }

    @Test
    public void testExecuteShouldSendExpectedMessage() {
        final String expected = "Welcome to Dungeons Online! You are playing as Player 1"
                + System.lineSeparator()
                + "For information about available commands, type 'help'."
                + System.lineSeparator();

        ResponseContents response = registerCommand.execute();
        final String result = response.message();

        assertEquals("execute should send expected message", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = registerCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client to send message", 1, clientsToSendMessage.size());
        assertEquals("send message to requesting player",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteMapShouldNotBeNull() {
        ResponseContents response = registerCommand.execute();
        char[][] map = response.updatedMap();

        assertNotNull("map should not be null", map);
    }

    @Test
    public void testExecuteShouldSendMapToEveryone() {
        ResponseContents response = registerCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("everyone should get map",
                game.getPlayersAddresses().size(),
                clientsToSendMap.size());
    }

    @Test
    public void testExecuteShouldMakeAPlayerExit() {
        ResponseContents response = registerCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("no player should exit when registering", exitingPlayerAddress);
    }
}
