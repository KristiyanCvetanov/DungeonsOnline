package bg.sofia.uni.fmi.mjt.dungeon.commands.hero;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.SocketAddress;
import java.util.Set;

public class CharacterInfoCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final Hero TEST_HERO = TestConfiguration.getTestHero();
    private Command characterInfoCommand;

    @Before
    public void setup() {
        characterInfoCommand = new CharacterInfoCommand(TEST_ADDRESS, TEST_HERO);
    }

    @Test
    public void testExecuteCheckMessageWithSpellOn() {
        String expected = getInfoWithSpell();

        ResponseContents response = characterInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with spell on", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithNoSpell() {
        String expected = getInfoWithNoSpell();

        characterInfoCommand = new CharacterInfoCommand(TEST_ADDRESS, new Hero("Pesho"));
        ResponseContents response = characterInfoCommand.execute();
        String result = response.message();

        assertEquals("check message with no spell", expected, result);
    }

    @Test
    public void testExecuteShouldReturnTheSameAnswerIfRepeated() {
        characterInfoCommand.execute();

        String expected = getInfoWithSpell();

        ResponseContents response = characterInfoCommand.execute();
        String result = response.message();

        assertEquals("message should be the same as before", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingPlayer() {
        ResponseContents response = characterInfoCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());

        assertEquals("requesting player should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        ResponseContents response = characterInfoCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        ResponseContents response = characterInfoCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        ResponseContents response = characterInfoCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should ne null", exitingPlayerAddress);
    }

    private String getInfoWithSpell() {
        return """
                Hero name: Test Hero
                Level: 1
                Experience: 0 (need for next level: 1000)
                Stats:
                    health: 100
                    mana: 100
                    attack: 85
                    defence: 50
                Weapon:
                    Treasure type: Weapon
                    Name: Test Weapon
                    Level: 1
                    Damage: 30
                Armor:
                    Treasure type: Armor
                    Name: Old Shirt
                    Level: 1
                    Defence: 0
                    Health: 0
                Spell:
                    Treasure type: Spell
                    Name: Test Spell
                    Level: 1
                    Damage: 35
                    Mana cost: 70
                    """;
    }

    private String getInfoWithNoSpell() {
        return """
                Hero name: Pesho
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
}
