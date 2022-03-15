package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import java.net.SocketAddress;
import java.util.Set;

public class ItemDrinkCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final int POTION_INDEX = 2;
    private final Hero testHero = TestConfiguration.getTestHero();
    private Command itemDrinkCommand;

    @Before
    public void setup() {
        itemDrinkCommand = new ItemDrinkCommand(TEST_ADDRESS, testHero, POTION_INDEX);
    }

    @Test
    public void testExecuteWithHealingPotionShouldRestoreHealth() {
        final int damageTaken = testHero.takeDamage(50);
        final int healingPoints = 30;

        itemDrinkCommand.execute();

        final int expected = 100 - damageTaken + healingPoints;
        final int result = testHero.getStats().getHealth();

        assertEquals("hero health should be the expected amount", expected, result);
    }

    @Test
    public void testExecuteWithHealingPotionShouldNotGoOverMaxHealth() {
        testHero.takeDamage(10);

        itemDrinkCommand.execute();

        int maxHealth = 100;
        assertEquals("potion should not heal over max health",
                maxHealth,
                testHero.getStats().getHealth());
    }

    @Test
    public void testExecuteWithManaPotionShouldRestoreMana() {
        testHero.attack();

        ManaPotion manaPotion = new ManaPotion("Test Potion", 30);
        testHero.addToBackpack(manaPotion);
        final int itemIndex = 3;

        itemDrinkCommand = new ItemDrinkCommand(TEST_ADDRESS, testHero, itemIndex);
        itemDrinkCommand.execute();

        final int expectedMana = 60;
        final int resultMana = testHero.getStats().getMana();

        assertEquals("mana should be the expected amount", expectedMana, resultMana);
    }

    @Test
    public void testExecuteWithHealingPotionShouldNotGoOverMaxMana() {
        testHero.attack();

        Treasure manaPotion = new ManaPotion("Test Potion", 120);
        testHero.addToBackpack(manaPotion);
        final int itemIndex = 3;

        itemDrinkCommand = new ItemDrinkCommand(TEST_ADDRESS, testHero, itemIndex);
        itemDrinkCommand.execute();

        final int expectedMana = 100;
        final int resultMana = testHero.getStats().getMana();

        assertEquals("mana should not go over max mana", expectedMana, resultMana);
    }

    @Test(expected = InvalidItemException.class)
    public void testExecuteShouldRemovePotionFromBackpack() {
        Treasure toDrink = testHero.getItem(POTION_INDEX);

        itemDrinkCommand.execute();

        testHero.dropItem(toDrink);
    }

    @Test
    public void testExecuteCheckMessageWithDrinkableItem() {
        String expected = "Test Potion drank." + System.lineSeparator();

        ResponseContents response = itemDrinkCommand.execute();
        String result = response.message();

        assertEquals("check message with drinkable item", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithNonDrinkableItem() {
        final int nonDrinkableIndex = 1;
        itemDrinkCommand = new ItemDrinkCommand(TEST_ADDRESS, testHero, nonDrinkableIndex);

        String expected = "Only potions can be drank." + System.lineSeparator();

        ResponseContents response = itemDrinkCommand.execute();
        String result = response.message();

        assertEquals("check message with non-drinkable item", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = itemDrinkCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());
        assertEquals("requesting client should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = itemDrinkCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = itemDrinkCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = itemDrinkCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }
}
