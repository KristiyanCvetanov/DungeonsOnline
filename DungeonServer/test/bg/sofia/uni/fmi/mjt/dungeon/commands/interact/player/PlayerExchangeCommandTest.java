package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.commands.move.MoveCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.map.Direction;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.List;
import java.util.Set;

public class PlayerExchangeCommandTest {

    private static final int ITEM_INDEX = 1;
    private static final Treasure EXCHANGE_ITEM = new ManaPotion("Test Potion", 50);
    private SocketAddress testAddressGiving;
    private SocketAddress testAddressReceiving;
    private GameState game;
    private Command playerExchangeCommand;

    @Before
    public void setup() {
        String mapStr = TestConfiguration.generateMapWithoutObstacles();
        game = TestConfiguration.generateGame(mapStr);

        List<SocketAddress> socketAddresses = TestConfiguration.getMultipleSocketAddresses();
        testAddressGiving = socketAddresses.get(0);
        testAddressReceiving = socketAddresses.get(1);

        TestConflictConfiguration.registerClientsInGame(testAddressGiving, testAddressReceiving, game);
        putExchangeItemInBackpack();
        TestConflictConfiguration.movePlayersToConflict(testAddressGiving, testAddressReceiving, game);

        playerExchangeCommand = new PlayerExchangeCommand(testAddressGiving, game, ITEM_INDEX);
    }

    @Test
    public void testExecuteShouldAddExchangeItemToReceivingPlayer() {
        Hero receivingHero = game.getHeroes().get(testAddressReceiving);

        playerExchangeCommand.execute();
        Treasure result = receivingHero.getItem(ITEM_INDEX);

        assertEquals("receiving player should get item", EXCHANGE_ITEM, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteShouldRemoveExchangeItemFromGivingPlayer() {
        Hero givingHero = game.getHeroes().get(testAddressGiving);

        playerExchangeCommand.execute();
        givingHero.getItem(ITEM_INDEX);
    }

    @Test
    public void testExecuteWithInvalidItemIndexShouldNotChangePlayers() {
        final int invalidIndex = 2;
        playerExchangeCommand = new PlayerExchangeCommand(testAddressGiving, game, invalidIndex);

        String oldBackpackGiving = game.getHeroes().get(testAddressGiving).getBackpackInfo();
        String oldBackpackReceiving = game.getHeroes().get(testAddressReceiving).getBackpackInfo();

        playerExchangeCommand.execute();

        String newBackpackGiving = game.getHeroes().get(testAddressGiving).getBackpackInfo();
        String newBackpackReceiving = game.getHeroes().get(testAddressReceiving).getBackpackInfo();

        assertEquals("giving player should not change bag",
                oldBackpackGiving,
                newBackpackGiving);
        assertEquals("receiving player should not change bag",
                oldBackpackReceiving,
                newBackpackReceiving);
    }

    @Test
    public void testExecuteWhenReceivingPlayerHasNoSpaceShouldNotChangePlayers() {
        Hero givingHero = game.getHeroes().get(testAddressGiving);
        Hero receivingHero = game.getHeroes().get(testAddressReceiving);
        fillBackpack(receivingHero, new Weapon("Test Weapon", 1, 10));

        String oldBackpackGiving = givingHero.getBackpackInfo();
        String oldBackpackReceiving = receivingHero.getBackpackInfo();

        playerExchangeCommand.execute();

        String newBackpackGiving = givingHero.getBackpackInfo();
        String newBackpackReceiving = receivingHero.getBackpackInfo();

        assertEquals("giving player should not change backpack",
                oldBackpackGiving,
                newBackpackGiving);
        assertEquals("receiving player should not change backpack",
                oldBackpackReceiving,
                newBackpackReceiving);
    }

    @Test
    public void testExecuteWhenNotInConflictShouldNotRemoveExchangeItem() {
        Coordinates givingCoord = game.getPlayersCoordinates().get(testAddressGiving);
        Direction validDirection = TestConflictConfiguration.getValidDirection(givingCoord, game);

        Command moveCommand = new MoveCommand(testAddressGiving, game, validDirection);
        moveCommand.execute();

        String oldBackpack = game.getHeroes().get(testAddressGiving).getBackpackInfo();
        playerExchangeCommand.execute();
        String newBackpack = game.getHeroes().get(testAddressGiving).getBackpackInfo();

        assertEquals("nothing should change when not in conflict", oldBackpack, newBackpack);
    }

    @Test
    public void testExecuteCheckMessageWithSuccessfulExchange() {
        String expected = "Player 1 gave Player 2 their Test Potion." + System.lineSeparator();

        ResponseContents response = playerExchangeCommand.execute();
        String result = response.message();

        assertEquals("check message with successful exchange", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithInvalidItemIndex() {
        final int invalidIndex = 2;
        playerExchangeCommand = new PlayerExchangeCommand(testAddressGiving, game, invalidIndex);

        String expected = "There is no such item in your backpack." + System.lineSeparator();

        ResponseContents response = playerExchangeCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid item index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWhenReceivingPlayerHasNoSpace() {
        Hero receivingHero = game.getHeroes().get(testAddressReceiving);
        fillBackpack(receivingHero, new HealingPotion("Test Potion", 20));

        String expected = "Player 2's backpack is full." + System.lineSeparator();

        ResponseContents response = playerExchangeCommand.execute();
        String result = response.message();

        assertEquals("check message when receiving player has no space",
                expected,
                result);
    }

    @Test
    public void testExecuteCheckMessageWhenNotInConflict() {
        Coordinates givingCoord = game.getPlayersCoordinates().get(testAddressGiving);
        Direction validDirection = TestConflictConfiguration.getValidDirection(givingCoord, game);

        Command moveCommand = new MoveCommand(testAddressGiving, game, validDirection);
        moveCommand.execute();

        String expected = "You are not in a position to exchange with anybody." + System.lineSeparator();

        ResponseContents response = playerExchangeCommand.execute();
        String result = response.message();

        assertEquals("check message when not in conflict", expected, result);
    }

    @Test
    public void testExecuteTwoClientsShouldReceiveMessageWithSuccessfulExchange() {
        ResponseContents response = playerExchangeCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("two players should get message when successful",
                2,
                clientsToSendMessage.size());
        assertTrue("giving player should receive message when successful",
                clientsToSendMessage.contains(testAddressGiving));
        assertTrue("receiving player should get message when successful",
                clientsToSendMessage.contains(testAddressReceiving));
    }

    @Test
    public void testExecuteTwoClientsShouldReceiveMessageWhenBackpackIsFull() {
        Hero receivingHero = game.getHeroes().get(testAddressReceiving);
        fillBackpack(receivingHero, new HealingPotion("Test Potion", 20));

        ResponseContents response = playerExchangeCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("two players should get message when backpack is full",
                2,
                clientsToSendMessage.size());
        assertTrue("giving player should receive message when backpack is full",
                clientsToSendMessage.contains(testAddressGiving));
        assertTrue("receiving player should get message when backpack is full",
                clientsToSendMessage.contains(testAddressReceiving));
    }

    @Test
    public void testExecuteOneClientShouldReceiveMessageWithInvalidItemIndex() {
        final int invalidIndex = 2;
        playerExchangeCommand = new PlayerExchangeCommand(testAddressGiving, game, invalidIndex);

        ResponseContents response = playerExchangeCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("one player should get message when item index is invalid",
                1,
                clientsToSendMessage.size());
        assertTrue("giving player should receive message when item index is invalid",
                clientsToSendMessage.contains(testAddressGiving));
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = playerExchangeCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = playerExchangeCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = playerExchangeCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }

    private void putExchangeItemInBackpack() {
        Hero givingHero = game.getHeroes().get(testAddressGiving);
        givingHero.addToBackpack(EXCHANGE_ITEM);
    }

    private void fillBackpack(Hero hero, Treasure t) {
        for (int i = 0; i < 10; i++) {
            hero.addToBackpack(t);
        }
    }
}
