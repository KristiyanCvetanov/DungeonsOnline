package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.commands.move.MoveCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
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

public class PlayersBattleCommandTest {

    private SocketAddress testAddress1;
    private SocketAddress testAddress2;
    private GameState game;
    private Command playersBattleCommand;

    @Before
    public void setup() {
        String mapStr = TestConfiguration.generateMapWithoutObstacles();
        game = TestConfiguration.generateGame(mapStr);

        List<SocketAddress> socketAddresses = TestConfiguration.getMultipleSocketAddresses();
        testAddress1 = socketAddresses.get(0);
        testAddress2 = socketAddresses.get(1);

        TestConflictConfiguration.registerClientsInGame(testAddress1, testAddress2, game);
        TestConflictConfiguration.movePlayersToConflict(testAddress1, testAddress2, game);

        playersBattleCommand = new PlayersBattleCommand(testAddress1, game);
    }

    @Test
    public void testExecuteShouldBeADeathMatch() {
        Hero hero1 = game.getHeroes().get(testAddress1);
        Hero hero2 = game.getHeroes().get(testAddress2);

        playersBattleCommand.execute();

        boolean condition = hero1.isAlive() ^ hero2.isAlive();
        assertTrue("only one should be alive after battle", condition);
    }

    @Test
    public void testExecuteShouldHaveNoEffectIfNotInConflict() {
        Coordinates givingCoord = game.getPlayersCoordinates().get(testAddress1);
        Direction validDirection = TestConflictConfiguration.getValidDirection(givingCoord, game);

        Command moveCommand = new MoveCommand(testAddress1, game, validDirection);
        moveCommand.execute();

        Hero hero = game.getHeroes().get(testAddress1);
        final int expected = hero.getStats().getHealth();

        playersBattleCommand.execute();
        final int result = hero.getStats().getHealth();

        assertEquals("should have no effect if not in conflict", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageToTwoClients() {
        ResponseContents response = playersBattleCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("the two players should receive message", 2, clientsToSendMessage.size());
        assertTrue("the first contender should receive message",
                clientsToSendMessage.contains(testAddress1));
        assertTrue("the second contender should receive message",
                clientsToSendMessage.contains(testAddress2));
    }

    @Test
    public void testExecuteShouldSendMapToEveryone() {
        ResponseContents response = playersBattleCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("everyone should get map",
                game.getPlayersAddresses().size(),
                clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldNotBeNull() {
        ResponseContents response = playersBattleCommand.execute();
        char[][] map = response.updatedMap();

        assertNotNull("map should not be null", map);
    }

    @Test
    public void testExecuteShouldMakeAPlayerExit() {
        ResponseContents response = playersBattleCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNotNull("address should not be null", exitingPlayerAddress);
    }
}
