package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.TestConfiguration;
import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.net.SocketAddress;
import java.util.Set;

public class ItemEquipCommandTest {

    private static final SocketAddress TEST_ADDRESS = TestConfiguration.getSingleClientAddress();
    private static final int WEAPON_INDEX = 1;
    private static final int ARMOR_INDEX = 3;
    private final Hero testHero = TestConfiguration.getTestHero();
    private Command itemEquipCommand;

    @Test
    public void testExecuteWithWeaponShouldChangeWeapon() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        Treasure toEquip = testHero.getItem(WEAPON_INDEX);
        Treasure oldWeapon = testHero.getWeapon();
        itemEquipCommand.execute();
        Treasure newWeapon = testHero.getWeapon();

        assertNotEquals("new weapon should differ from old one", oldWeapon, newWeapon);
        assertEquals("new weapon should be the one to equip", toEquip, newWeapon);
    }

    @Test
    public void testExecuteWithArmorShouldChangeArmor() {
        Armor toCollect = new Armor("Test Armor", 1, 20, 30);
        toCollect.collect(testHero);

        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, ARMOR_INDEX);

        Treasure toEquip = testHero.getItem(ARMOR_INDEX);
        Treasure oldArmor = testHero.getArmor();
        itemEquipCommand.execute();
        Treasure newArmor = testHero.getArmor();

        assertNotEquals("new armor should differ from old one", oldArmor, newArmor);
        assertEquals("new armor should be the one to equip", toEquip, newArmor);
    }

    @Test
    public void testExecuteShouldPutOldGearInBackpack() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        Treasure oldWeapon = testHero.getWeapon();
        itemEquipCommand.execute();
        Treasure fromBackpack = testHero.getItem(WEAPON_INDEX);

        assertEquals("execute should put old gear in backpack", oldWeapon, fromBackpack);
    }

    @Test(expected = InvalidItemException.class)
    public void testExecuteShouldRemoveEquippedGearFromBackpack() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        Treasure toEquip = testHero.getItem(WEAPON_INDEX);
        assertNotNull("item should still be in backpack", toEquip);

        itemEquipCommand.execute();
        testHero.dropItem(toEquip);
    }

    @Test
    public void testExecuteShouldNotEquipAHigherLevelItem() {
        Armor toCollect = new Armor("Test Armor", 2, 20, 30);
        toCollect.collect(testHero);

        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, ARMOR_INDEX);

        Treasure oldArmor = testHero.getArmor();
        itemEquipCommand.execute();
        Treasure newArmor = testHero.getArmor();
        assertEquals("gear should not have changed", oldArmor, newArmor);

        assertEquals("higher level gear should still be in backpack",
                toCollect,
                testHero.getItem(ARMOR_INDEX));
    }

    @Test
    public void testExecuteWithInvalidItemIndex() {
        final int itemIndex = 7;
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, itemIndex);

        Treasure oldWeapon = testHero.getWeapon();
        Treasure oldArmor = testHero.getArmor();
        String oldBackpackInfo = testHero.getBackpackInfo();

        itemEquipCommand.execute();

        Treasure newWeapon = testHero.getWeapon();
        assertEquals("weapon should be old one", oldWeapon, newWeapon);

        Treasure newArmor = testHero.getArmor();
        assertEquals("armor should be old one", oldArmor, newArmor);

        String newBackPackInfo = testHero.getBackpackInfo();
        assertEquals("backpack should not have changed", oldBackpackInfo, newBackPackInfo);
    }

    @Test
    public void testExecuteCheckMessageWithValidItemIndex() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        String expected = "Bare Fists equipped." + System.lineSeparator();

        ResponseContents response = itemEquipCommand.execute();
        String result = response.message();

        assertEquals("check message with valid item index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithInvalidItemIndex() {
        final int itemIndex = 7;
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, itemIndex);

        String expected = "There is no such item in your backpack." + System.lineSeparator();

        ResponseContents response = itemEquipCommand.execute();
        String result = response.message();

        assertEquals("check message with invalid item index", expected, result);
    }

    @Test
    public void testExecuteCheckMessageWithNonEquippableItem() {
        final int nonEquippableIndex = 2;
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, nonEquippableIndex);

        String expected = "You can equip only weapons and armor." + System.lineSeparator();

        ResponseContents response = itemEquipCommand.execute();
        String result = response.message();

        assertEquals("check message with non-equippable item", expected, result);
    }

    @Test
    public void testExecuteShouldSendMessageOnlyToRequestingPlayer() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        ResponseContents response = itemEquipCommand.execute();
        Set<SocketAddress> clientsToSendMessage = response.clientsToSendMessage();

        assertEquals("only one client should be sent message", 1, clientsToSendMessage.size());
        assertEquals("requesting player should be sent message",
                TEST_ADDRESS,
                clientsToSendMessage.iterator().next());
    }

    @Test
    public void testExecuteShouldNotSendMapToAnyone() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        ResponseContents response = itemEquipCommand.execute();
        Set<SocketAddress> clientsToSendMap = response.clientsToSendMap();

        assertEquals("no one should be sent map", 0, clientsToSendMap.size());
    }

    @Test
    public void testExecuteMapShouldBeNull() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        ResponseContents response = itemEquipCommand.execute();
        char[][] map = response.updatedMap();

        assertNull("map should be null", map);
    }

    @Test
    public void testExecuteShouldNotMakeAPlayerExit() {
        itemEquipCommand = new ItemEquipCommand(TEST_ADDRESS, testHero, WEAPON_INDEX);

        ResponseContents response = itemEquipCommand.execute();
        SocketAddress exitingPlayerAddress = response.exitingPlayerAddress();

        assertNull("address should be null", exitingPlayerAddress);
    }
}
