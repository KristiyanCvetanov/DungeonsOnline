package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.Set;

public class ItemLearnCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final int SPELL_INDEX = 3;
    private final Hero testHero = TestConfiguration.getTestHero();
    private Command itemLearnCommand;

    @Before
    public void setup() {
        Spell levelOneSpell = new Spell("Test Spell1", 1, 20, 50);
        Spell higherLevelSpell = new Spell("Test Spell2", 2, 30, 70);
        testHero.addToBackpack(levelOneSpell);
        testHero.addToBackpack(higherLevelSpell);

        itemLearnCommand = new ItemLearnCommand(TEST_ADDRESS, testHero, SPELL_INDEX);
    }

    @Test
    public void testExecuteShouldChangeSpell() {
        Treasure toLearn = testHero.getItem(SPELL_INDEX);
        Treasure oldSpell = testHero.getSpell();

        itemLearnCommand.execute();

        Treasure newSpell = testHero.getSpell();

        assertNotEquals("new spell should differ from old one", oldSpell, newSpell);
        assertEquals("new spell should be learned spell", toLearn, newSpell);
    }

    @Test
    public void testExecuteWithHigherLevelSpell() {
        final int highLevelSpellIndex = 4;
        itemLearnCommand = new ItemLearnCommand(TEST_ADDRESS, testHero, highLevelSpellIndex);

        Treasure oldSpell = testHero.getSpell();
        String oldBackpackInfo = testHero.getBackpackInfo();
        itemLearnCommand.execute();

        Treasure newSpell = testHero.getSpell();
        assertEquals("spell should be the same", oldSpell, newSpell);

        String newBackpackInfo = testHero.getBackpackInfo();
        assertEquals("backpack should be the same", oldBackpackInfo, newBackpackInfo);
    }

    @Test(expected = InvalidItemException.class)
    public void testExecuteShouldRemoveLearnedSpellFromBackpack() {
        Treasure toLearn = testHero.getItem(SPELL_INDEX);

        itemLearnCommand.execute();

        testHero.dropItem(toLearn);
    }

    @Test(expected = InvalidItemException.class)
    public void testExecuteShouldNotPutOldSpellInBackpack() {
        Treasure oldSpell = testHero.getSpell();

        itemLearnCommand.execute();

        testHero.dropItem(oldSpell);
    }

    @Test
    public void testExecuteCheckMessageWithValidSpellIndex() {
        String expected = "Test Spell1 learned." + System.lineSeparator();

        ResponseContents response = itemLearnCommand.execute();
        String result = response.message();

        assertEquals("check message with valid spell index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithInvalidSpellIndex() {
        final int invalidItemIndex = 7;
        itemLearnCommand = new ItemLearnCommand(TEST_ADDRESS, testHero, invalidItemIndex);

        String expected = "There is no such item in your backpack." + System.lineSeparator();

        ResponseContents response = itemLearnCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid spell index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithNonLearnableItem() {
        final int nonLearnableIndex = 2;
        itemLearnCommand = new ItemLearnCommand(TEST_ADDRESS, testHero, nonLearnableIndex);

        String expected = "You can learn only spells." + System.lineSeparator();

        ResponseContents response = itemLearnCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid spell index", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingClient() {
        ResponseContents response = itemLearnCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());
        assertEquals("requesting client should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = itemLearnCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = itemLearnCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = itemLearnCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }
}

