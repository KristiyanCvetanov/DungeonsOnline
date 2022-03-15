package bg.sofia.uni.fmi.mjt.dungeon.commands.move;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player.TestConflictConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.commands.register.RegisterCommand;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.net.SocketAddress;
import java.util.List;
import java.util.Set;

public class MoveCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private GameState game;
    private Direction dir;
    private Coordinates oldCoordinates;
    private Coordinates nextLocation;
    private Command moveCommand;

    @Before
    public void setup() {
        String mapStr = TestConfiguration.generateMapWithoutObstacles();
        game = TestConfiguration.generateGame(mapStr);

        Command registerCommand = new RegisterCommand(TEST_ADDRESS, game);
        registerCommand.execute();

        oldCoordinates = game.getPlayersCoordinates().get(TEST_ADDRESS);
        dir = TestConflictConfiguration.getValidDirection(oldCoordinates, game);
        moveCommand = new MoveCommand(TEST_ADDRESS, game, dir);

        nextLocation = TestConflictConfiguration.getNextLocationCoords(oldCoordinates, dir);
    }

    @Test
    public void testExecuteWhenMovingToFreeSpot() {
        moveCommand.execute();

        char expectedOldLocation = '.';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be a free spot", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = '1';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be with player icon", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingToObstacle() {
        game.getMap().markVictorious(nextLocation, '#');
        moveCommand.execute();

        char expectedOldLocation = '1';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be with player icon", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = '#';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be still obstacle", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingToTreasure() {
        game.getMap().markVictorious(nextLocation, 'T');
        moveCommand.execute();

        char expectedOldLocation = '.';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be with free spot", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = '1';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be player icon", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingToMinion() {
        game.getMap().markVictorious(nextLocation, 'M');
        moveCommand.execute();

        char expectedOldLocation = '.';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be with free spot", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = '1';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be player icon", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingToPlayer() {
        List<SocketAddress> addressList = TestConfiguration.getMultipleSocketAddresses();
        SocketAddress otherPlayerAddress = addressList.get(0);

        Command registerCommand = new RegisterCommand(otherPlayerAddress, game);
        registerCommand.execute();
        moveOtherPlayerNextToTestPlayer(otherPlayerAddress);

        moveCommand.execute();

        char expectedOldLocation = '.';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be with free spot", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = 'C';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be conflict", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingToConflict() {
        game.getMap().markVictorious(nextLocation, 'C');
        moveCommand.execute();

        char expectedOldLocation = '1';
        char resultOldLocation = game.getMap().getContent()[oldCoordinates.y()][oldCoordinates.x()];
        assertEquals("old spot should be with player icon", expectedOldLocation, resultOldLocation);

        char expectedNewLocation = 'C';
        char resultNewLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("new spot should be conflict", expectedNewLocation, resultNewLocation);
    }

    @Test
    public void testExecuteWhenMovingOutOfConflict() {
        List<SocketAddress> addressList = TestConfiguration.getMultipleSocketAddresses();
        SocketAddress otherPlayerAddress = addressList.get(0);

        Command registerCommand = new RegisterCommand(otherPlayerAddress, game);
        registerCommand.execute();
        moveOtherPlayerNextToTestPlayer(otherPlayerAddress);

        moveCommand.execute();
        moveCommand.execute();

        char expectedOldLocation = '2';
        char resultOldLocation = game.getMap().getContent()[nextLocation.y()][nextLocation.x()];
        assertEquals("old spot should be with other player icon", expectedOldLocation, resultOldLocation);
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToFreeSpot() {
        String expected = "";
        String result = moveCommand.execute().message();

        assertEquals("check message when moving to free spot", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToObstacle() {
        game.getMap().markVictorious(nextLocation, '#');
        String expected = "There is an obstacle on the way." + System.lineSeparator();
        String result = moveCommand.execute().message();

        assertEquals("check message when moving to obstacle", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToTreasure() {
        game.getMap().markVictorious(nextLocation, 'T');
        final String suffix = "collected successfully in backpack." + System.lineSeparator();
        String result = moveCommand.execute().message();

        assertTrue("check message when moving to treasure", result.endsWith(suffix));
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToMinion() {
        game.getMap().markVictorious(nextLocation, 'M');
        final String prefix = "A battle between ";
        String result = moveCommand.execute().message();

        assertTrue("check message prefix when moving to minion", result.startsWith(prefix));
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToPlayer() {
        List<SocketAddress> addressList = TestConfiguration.getMultipleSocketAddresses();
        SocketAddress otherPlayerAddress = addressList.get(0);

        Command registerCommand = new RegisterCommand(otherPlayerAddress, game);
        registerCommand.execute();
        moveOtherPlayerNextToTestPlayer(otherPlayerAddress);

        String expected = "You are now in conflict! Commands 'attack' and 'give' are active."
                + System.lineSeparator();
        String result = moveCommand.execute().message();

        assertEquals("check message when moving to player", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWhenMovingToConflict() {
        game.getMap().markVictorious(nextLocation, 'C');
        final String expected = "You cannot join other players' conflict." + System.lineSeparator();
        String result = moveCommand.execute().message();

        assertEquals("check message when moving to conflict", expected, result);
    }

    @Test
    public void testExecuteShouldNotSendMessageToAnyoneWhenMovingToFreeSpot() {
        Set<SocketAddress> clientsToSendMessage = moveCommand.execute().clientsToSendMessage();

        assertEquals("no one should get message", 0, clientsToSendMessage.size());
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingPlayerWhenMovingToObstacle() {
        game.getMap().markVictorious(nextLocation, '#');
        Set<SocketAddress> clientsToSendMessage = moveCommand.execute().clientsToSendMessage();

        assertEquals("only one client should get message", 1, clientsToSendMessage.size());
        assertEquals("moving player should get message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingPlayerWhenMovingToTreasure() {
        game.getMap().markVictorious(nextLocation, 'T');
        Set<SocketAddress> clientsToSendMessage = moveCommand.execute().clientsToSendMessage();

        assertEquals("only one client should get message", 1, clientsToSendMessage.size());
        assertEquals("moving player should get message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingPlayerWhenMovingToMinion() {
        game.getMap().markVictorious(nextLocation, 'M');
        Set<SocketAddress> clientsToSendMessage = moveCommand.execute().clientsToSendMessage();

        assertEquals("only one client should get message", 1, clientsToSendMessage.size());
        assertEquals("moving player should get message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldSendMessageToBothPlayersWhenMovingToPlayer() {
        List<SocketAddress> addressList = TestConfiguration.getMultipleSocketAddresses();
        SocketAddress otherPlayerAddress = addressList.get(0);

        Command registerCommand = new RegisterCommand(otherPlayerAddress, game);
        registerCommand.execute();
        moveOtherPlayerNextToTestPlayer(otherPlayerAddress);

        Set<SocketAddress> clientsToSendMessage = moveCommand.execute().clientsToSendMessage();

        assertEquals("two clients should get message", 2, clientsToSendMessage.size());
        assertTrue("moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));
        assertTrue("static player should get message",
                clientsToSendMessage.contains(otherPlayerAddress));
    }

    @Test
    public void testExecuteShouldSendMapToEveryone() {
        Set<SocketAddress> clientsToSendMap = moveCommand.execute().clientsToSendMap();

        assertEquals("everyone should get message",
                game.getPlayersAddresses().size(),
                clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldNotBeNull() {
        char[][] map = moveCommand.execute().updatedMap();

        assertNotNull("map should not be null", map);
    }

    private void moveOtherPlayerNextToTestPlayer(SocketAddress otherPlayerAddress) {
        Coordinates otherPlayerCoords = game.getPlayersCoordinates().get(otherPlayerAddress);
        Coordinates shift = new Coordinates(nextLocation.x() - otherPlayerCoords.x(),
                nextLocation.y() - otherPlayerCoords.y());

        final int x = shift.x();
        final int y = shift.y();

        final Direction horizontalDir = x > 0 ? Direction.RIGHT : Direction.LEFT;
        final Direction verticalDir = y > 0 ? Direction.DOWN : Direction.UP;

        Command newMoveCommand = new MoveCommand(otherPlayerAddress, game, horizontalDir);
        for (int i = 0; i < Math.abs(x); i++) {
            newMoveCommand.execute();
        }

        newMoveCommand = new MoveCommand(otherPlayerAddress, game, verticalDir);
        for (int i = 0; i < Math.abs(y); i++) {
            newMoveCommand.execute();
        }
    }
}
