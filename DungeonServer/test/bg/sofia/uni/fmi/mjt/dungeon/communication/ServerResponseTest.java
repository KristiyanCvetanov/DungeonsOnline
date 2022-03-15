package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player.TestConflictConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Set;

public class ServerResponseTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final SocketAddress TEST_ADDRESS_HELP = TestConfiguration.getMultipleSocketAddresses().get(3);
    private GameState game;
    private ServerResponse serverResponse;

    @Before
    public void setup() {
        String mapInfo = TestConfiguration.generateMapWithoutObstacles();
        game = TestConfiguration.generateGame(mapInfo);

        serverResponse = new ServerResponse(game);

        // add some player to game
        serverResponse.formulateAnswer("register", TEST_ADDRESS_HELP);
    }

    @Test
    public void testFormulateAnswerWithRegisterCommand() {
        final String request = "register" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = getWelcomeMessage();
        assertEquals("register - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("register - only one client should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("register - registered client should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("register - everyone should get map",
                game.getPlayersAddresses().keySet(),
                clientsToSendMap);

        assertNotNull("register - map should not be null", response.updatedMap());

        assertNull("register - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithHelpCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "help" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = TestConfiguration.getCommandsInfo();
        assertEquals("help valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("help valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("help valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("help valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("help valid - map should be null", response.updatedMap());

        assertNull("help valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithHelpCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "    help  " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = TestConfiguration.getCommandsInfo();
        assertEquals("help whitespaces - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("help whitespaces - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("help whitespaces - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("help whitespaces - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("help whitespaces - map should be null", response.updatedMap());

        assertNull("help whitespaces - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithHelpCommandWithMoreArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "help foo bar" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'help' command needs no arguments." + System.lineSeparator();
        assertEquals("help more args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("help more args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("help more args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("help more args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("help more args - map should be null", response.updatedMap());

        assertNull("help more args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithCharacterCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "character" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = getCharacterInfo();
        assertEquals("character valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("character valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("character valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("character valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("character valid - map should be null", response.updatedMap());

        assertNull("character valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithCharacterCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = " character     " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = getCharacterInfo();
        assertEquals("character whitespaces - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("character whitespaces - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("character whitespaces - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("character whitespaces - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("character whitespaces - map should be null", response.updatedMap());

        assertNull("character whitespaces - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithCharacterCommandWithMoreArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "character baz bar" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'character' command needs no arguments." + System.lineSeparator();
        assertEquals("character more args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("character more args - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("character more args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("character more args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("character more args - map should be null", response.updatedMap());

        assertNull("character more args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithBackpackCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "backpack" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Backpack is empty." + System.lineSeparator();
        assertEquals("backpack valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("backpack valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("backpack valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("backpack valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("backpack valid - map should be null", response.updatedMap());

        assertNull("backpack valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithBackpackCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "     backpack" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Backpack is empty." + System.lineSeparator();
        assertEquals("backpack whitespaces - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("backpack whitespaces - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("backpack whitespaces - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("backpack whitespaces - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("backpack whitespaces - map should be null", response.updatedMap());

        assertNull("backpack whitespaces - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithBackpackCommandWithMoreArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "backpack more args" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'backpack' command needs no arguments." + System.lineSeparator();
        assertEquals("backpack more args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("backpack more args - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("backpack more args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("backpack more args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("backpack more args - map should be null", response.updatedMap());

        assertNull("backpack more args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToFreeSpot() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        assertEquals("move to free spot - check message", "", response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertTrue("move to free spot - no one should get message", clientsToSendMessage.isEmpty());

        assertEquals("move to free spot - everyone should get map",
                game.getPlayersAddresses().keySet(),
                response.clientsToSendMap());

        assertNotNull("move to free spot - map should not be null", response.updatedMap());
        assertNull("move to free spot - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToFreeSpotWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        final String request = "    move   " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        assertEquals("move whitespaces - check message", "", response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertTrue("move whitespaces - no one should get message", clientsToSendMessage.isEmpty());

        assertEquals("move whitespaces - everyone should get map",
                game.getPlayersAddresses().keySet(),
                response.clientsToSendMap());

        assertNotNull("move whitespaces - map should not be null", response.updatedMap());
        assertNull("move whitespaces - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "move" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'move' command has 1 argument." + System.lineSeparator();
        assertEquals("move no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("moving player should get message", clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("move no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("move no args - map should be null", response.updatedMap());
        assertNull("move no args - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandWithInvalidDirection() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "move foo" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Invalid direction." + System.lineSeparator();
        assertEquals("move direction - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move direction - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("moving player should get message", clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("move direction - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("move direction - map should be null", response.updatedMap());
        assertNull("move direction - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToObstacle() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, '#');

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is an obstacle on the way." + System.lineSeparator();
        assertEquals("move to obstacle - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to obstacle - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to obstacle - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertTrue("move to obstacle - no one should get map", clientsToSendMap.isEmpty());

        assertNull("move to obstacle - map should be null", response.updatedMap());

        assertNull("move to obstacle - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToTreasureNormalCase() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, 'T');

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String suffix = "collected successfully in backpack." + System.lineSeparator();
        assertTrue("move to treasure - check message", response.message().endsWith(suffix));

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to treasure - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to treasure - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("move to treasure - everyone should get map",
                game.getPlayersAddresses().keySet(),
                clientsToSendMap);

        assertNotNull("move to treasure - map should not be null", response.updatedMap());

        assertNull("move to treasure - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToTreasureWhenBackpackIsFull() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, 'T');
        fillBackpack();

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Your backpack is full!" + System.lineSeparator();
        assertEquals("move to treasure - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to treasure - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to treasure - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("move to treasure - everyone should get map",
                game.getPlayersAddresses().keySet(),
                clientsToSendMap);

        assertNotNull("move to treasure - map should not be null", response.updatedMap());

        assertNull("move to treasure - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToMinionNormalCase() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, 'M');

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String prefix = "A battle between";
        assertTrue("move to minion - check message", response.message().startsWith(prefix));

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to minion - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to minion - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("move to minion - everyone should get map",
                game.getPlayersAddresses().keySet(),
                clientsToSendMap);

        assertNotNull("move to minion - map should not be null", response.updatedMap());

        assertNull("move to minion - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToMinionWhenLowHealth() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, 'M');
        game.getHeroes().get(TEST_ADDRESS).takeDamage(105);

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String prefix = "A battle between";
        assertTrue("move to minion - check message", response.message().startsWith(prefix));

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to minion - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to minion - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertEquals("move to minion - everyone should get map",
                game.getPlayersAddresses().keySet(),
                clientsToSendMap);

        assertNotNull("move to minion - map should not be null", response.updatedMap());

        assertNotNull("move to minion - address should not be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithMoveCommandToConflict() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        Coordinates playerCoords = game.getPlayersCoordinates().get(TEST_ADDRESS);
        Direction dir = TestConflictConfiguration.getValidDirection(playerCoords, game);

        Coordinates nextLocation = TestConflictConfiguration.getNextLocationCoords(playerCoords, dir);
        game.getMap().markVictorious(nextLocation, 'C');

        final String request = "move " + getDirectionAsString(dir) + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You cannot join other players' conflict." + System.lineSeparator();
        assertEquals("move to conflict - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("move to conflict - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("move to conflict - moving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();
        assertTrue("move to conflict - no one should get map", clientsToSendMap.isEmpty());

        assertNull("move to conflict - map should be null", response.updatedMap());

        assertNull("move to conflict - address should be null", response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithInfoCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "info 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = getInfoMessage();
        assertEquals("info valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("info valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("info valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("info valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("info valid - map should be null", response.updatedMap());

        assertNull("info valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithInfoCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "   info    1   " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = getInfoMessage();
        assertEquals("info whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("info whitespace - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("info whitespace - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("info whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("info whitespace - map should be null", response.updatedMap());

        assertNull("info whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithInfoCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "info" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'info' command has 1 argument." + System.lineSeparator();
        assertEquals("info no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("info no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("info no args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("info no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("info no args - map should be null", response.updatedMap());

        assertNull("info no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithInfoCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "info 3" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("info index - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("info index - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("info index - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("info index - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("info index - map should be null", response.updatedMap());

        assertNull("info index - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Weapon("Test Weapon", 1, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "equip 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Weapon equipped." + System.lineSeparator();
        assertEquals("equip valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip valid - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("equip valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip valid - map should be null", response.updatedMap());

        assertNull("equip valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWhenWeaponIsHigherLevel() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Weapon("Test Weapon", 2, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "equip 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You don't have the required level to equip that item."
                + System.lineSeparator();
        assertEquals("equip level - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip level - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("equip level - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip level - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip level - map should be null", response.updatedMap());

        assertNull("equip level - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWithNonEquippableItem() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect1 = new HealingPotion("Test Potion", 30);
        Treasure toCollect2 = new Weapon("Test Weapon", 1, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect1);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect2);

        final String request = "equip 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You can equip only weapons and armor."
                + System.lineSeparator();
        assertEquals("equip potion - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip potion - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("equip potion - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip potion - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip potion - map should be null", response.updatedMap());

        assertNull("equip potion - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Weapon("Test Weapon", 1, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "  equip        1  " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Weapon equipped." + System.lineSeparator();
        assertEquals("equip whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip whitespace - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("equip whitespace - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip whitespace - map should be null", response.updatedMap());

        assertNull("equip whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "equip" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'equip' command has 1 argument." + System.lineSeparator();
        assertEquals("equip no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("equip no args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip no args - map should be null", response.updatedMap());

        assertNull("equip no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithEquipCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "equip 5" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("equip index - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("equip index - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("equip index - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("equip index - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("equip index - map should be null", response.updatedMap());

        assertNull("equip index - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Spell("Test Spell", 1, 50, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "learn 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Spell learned." + System.lineSeparator();
        assertEquals("learn valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("learn valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn valid - map should be null", response.updatedMap());

        assertNull("learn valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWhenSpellIsHigherLevel() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Spell("Test Spell", 2, 50, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "learn 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You don't have the required level to learn that spell."
                + System.lineSeparator();
        assertEquals("learn level - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn level - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("learn level - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn level - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn level - map should be null", response.updatedMap());

        assertNull("learn level - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWithNonLearnableItem() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "learn 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You can learn only spells." + System.lineSeparator();
        assertEquals("learn potion - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn potion - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("learn potion - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn potion - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn potion - map should be null", response.updatedMap());

        assertNull("learn potion - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Spell("Test Spell", 1, 50, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = " learn 1   " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Spell learned." + System.lineSeparator();
        assertEquals("learn whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn whitespace - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("learn whitespace - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn whitespace - map should be null", response.updatedMap());

        assertNull("learn whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "learn" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'learn' command has 1 argument." + System.lineSeparator();
        assertEquals("learn no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("learn no args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn no args - map should be null", response.updatedMap());

        assertNull("learn no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithLearnCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "learn 5" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("learn index - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("learn index - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("learn index - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("learn index - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("learn index - map should be null", response.updatedMap());

        assertNull("learn index - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithDrinkCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "drink 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Potion drank." + System.lineSeparator();
        assertEquals("drink valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("drink valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("drink valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("drink valid - map should be null", response.updatedMap());

        assertNull("drink valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithDrinkCommandWithNonDrinkableItem() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new Spell("Test Spell", 1, 50, 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "drink 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Only potions can be drank." + System.lineSeparator();
        assertEquals("drink spell - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("drink spell - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink spell - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("drink spell - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("drink spell - map should be null", response.updatedMap());

        assertNull("drink spell - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithDrinkCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "        drink  1 " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Potion drank." + System.lineSeparator();
        assertEquals("drink whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("drink whitespace - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink whitespace - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("drink whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("drink whitespace - map should be null", response.updatedMap());

        assertNull("drink whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithDrinkCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "drink" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'drink' command has 1 argument." + System.lineSeparator();
        assertEquals("drink no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("drink no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink no args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("drink no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("drink no args - map should be null", response.updatedMap());

        assertNull("drink no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithDrinkCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "drink 7" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("drink index - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("drink index - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink index - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("drink index - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("drink index - map should be null", response.updatedMap());

        assertNull("drink index - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithThrowCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "throw 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Potion thrown away." + System.lineSeparator();
        assertEquals("throw valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("throw valid - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("drink valid - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("throw valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("throw valid - map should be null", response.updatedMap());

        assertNull("throw valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithThrowCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "        throw  1 " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Test Potion thrown away." + System.lineSeparator();
        assertEquals("throw whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("throw whitespace - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("throw whitespace - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("throw whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("throw whitespace - map should be null", response.updatedMap());

        assertNull("throw whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithThrowCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "throw" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'throw' command has 1 argument." + System.lineSeparator();
        assertEquals("throw no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("throw no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("throw no args - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("throw no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("throw no args - map should be null", response.updatedMap());

        assertNull("throw no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithThrowCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        Treasure toCollect = new HealingPotion("Test Potion", 50);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "throw 7" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("throw index - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("throw index - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("throw index - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("throw index - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("throw index - map should be null", response.updatedMap());

        assertNull("throw index - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithAttackCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);

        final String request = "attack" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("attack valid - two should get message", 2, clientsToSendMessage.size());
        assertTrue("attack valid - attacking player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));
        assertTrue("attack valid - defending player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS_HELP));

        assertEquals("attack valid - everyone should get map",
                game.getPlayersAddresses().keySet(),
                response.clientsToSendMap());

        assertNotNull("attack valid - map should not be null", response.updatedMap());

        assertNotNull("attack valid - address of exiting player should not be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithAttackCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);

        final String request = "    attack " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("attack whitespaces - two should get message", 2, clientsToSendMessage.size());
        assertTrue("attack whitespaces - attacking player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));
        assertTrue("attack whitespaces - defending player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS_HELP));

        assertEquals("attack whitespaces - everyone should get map",
                game.getPlayersAddresses().keySet(),
                response.clientsToSendMap());

        assertNotNull("attack whitespaces - map should not be null", response.updatedMap());

        assertNotNull("attack whitespaces - address of exiting player should not be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithAttackCommandWithMoreArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);

        final String request = "attack more args" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'attack' command needs no arguments." + System.lineSeparator();
        assertEquals("attack more args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("attack more args - only one should get message",
                1,
                clientsToSendMessage.size());
        assertTrue("attack more args - attacking player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("attack more args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("attack more args - map should be null", response.updatedMap());

        assertNull("attack more args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithAttackCommandWhenNotInConflict() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "attack" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You are not in a position to attack anybody."
                + System.lineSeparator();
        assertEquals("attack conflict - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("attack conflict - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("attack conflict - attacking player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("attack conflict - no one should get map", response.clientsToSendMap().isEmpty());

        System.out.println(Arrays.deepToString(response.updatedMap()));
        assertNull("attack conflict - map should be null", response.updatedMap());

        assertNull("attack conflict - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithGiveCommandWhenValid() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);
        Treasure toCollect = new Weapon("Test Weapon", 1, 20);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "give 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Player 2 gave Player 1 their Test Weapon." + System.lineSeparator();
        assertEquals("give valid - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("give valid - two should get message", 2, clientsToSendMessage.size());
        assertTrue("give valid - giving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));
        assertTrue("give valid - receiving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS_HELP));

        assertTrue("give valid - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("give valid - map should be null", response.updatedMap());

        assertNull("give valid - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithGiveCommandWithWhitespaces() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);
        Treasure toCollect = new Weapon("Test Weapon", 1, 20);
        game.getHeroes().get(TEST_ADDRESS).addToBackpack(toCollect);

        final String request = "    give  1 " + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Player 2 gave Player 1 their Test Weapon." + System.lineSeparator();
        assertEquals("give whitespace - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("give whitespace - two should get message", 2, clientsToSendMessage.size());
        assertTrue("give whitespace - giving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));
        assertTrue("give whitespace - receiving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS_HELP));

        assertTrue("give whitespace - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("give whitespace - map should be null", response.updatedMap());

        assertNull("give whitespace - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithGiveCommandWithNoArguments() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);

        final String request = "give" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "The 'give' command has 1 argument." + System.lineSeparator();
        assertEquals("give no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("give no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("give no args - giving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("give no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("give no args - map should be null", response.updatedMap());

        assertNull("give no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithGiveCommandWithInvalidItemIndex() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);
        TestConflictConfiguration.movePlayersToConflict(TEST_ADDRESS, TEST_ADDRESS_HELP, game);

        final String request = "give 8" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "There is no such item in your backpack." + System.lineSeparator();
        assertEquals("give no args - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("give no args - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("give no args - giving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("give no args - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("give no args - map should be null", response.updatedMap());

        assertNull("give no args - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithGiveCommandWhenNotInConflict() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "give 1" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "You are not in a position to exchange with anybody."
                + System.lineSeparator();
        assertEquals("give conflict - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("give conflict - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("give conflict - giving player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("give conflict - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("give conflict - map should be null", response.updatedMap());

        assertNull("give conflict - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    @Test
    public void testFormulateAnswerWithUnknownCommand() {
        serverResponse.formulateAnswer("register", TEST_ADDRESS);

        final String request = "foo bar baz" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, TEST_ADDRESS);

        final String expectedMessage = "Unknown command." + System.lineSeparator();
        assertEquals("unknown - check message", expectedMessage, response.message());

        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();
        assertEquals("unknown - only one should get message", 1, clientsToSendMessage.size());
        assertTrue("unknown - requesting player should get message",
                clientsToSendMessage.contains(TEST_ADDRESS));

        assertTrue("unknown - no one should get map", response.clientsToSendMap().isEmpty());

        assertNull("unknown - map should be null", response.updatedMap());

        assertNull("unknown - address of exiting player should be null",
                response.exitingPlayerAddress());
    }

    private String getCharacterInfo() {
        return """
                Hero name: Player 2
                Level: 1
                Experience: 0 (need for next level: 1000)
                Stats:
                    health: 100
                    mana: 100
                    attack: 50
                    defence: 50
                Weapon:
                    Treasure type: Weapon
                    Name: Bare Fists
                    Level: 1
                    Damage: 0
                Armor:
                    Treasure type: Armor
                    Name: Old Shirt
                    Level: 1
                    Defence: 0
                    Health: 0
                Spell:
                    None
                    """;
    }

    private String getWelcomeMessage() {
        return "Welcome to Dungeons Online! You are playing as Player 2"
                + System.lineSeparator()
                + "For information about available commands, type 'help'."
                + System.lineSeparator();
    }

    private String getInfoMessage() {
        return """
                    Treasure type: Healing potion
                    Name: Test Potion
                    Healing points: 50
                """;
    }

    private String getDirectionAsString(Direction dir) {
        return switch (dir) {
            case UP -> "up";
            case DOWN -> "down";
            case LEFT -> "left";
            case RIGHT -> "right";
        };
    }

    private void fillBackpack() {
        Hero hero = game.getHeroes().get(TEST_ADDRESS);

        final int bagCapacity = 10;
        for(int i = 0; i < 10; i++) {
            hero.addToBackpack(new HealingPotion("Test Potion", 50));
        }
    }
}
