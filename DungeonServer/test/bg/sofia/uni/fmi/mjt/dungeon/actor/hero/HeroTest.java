package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.Drinkable;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class HeroTest {

    private static final Weapon TEST_WEAPON = new Weapon("Test Weapon", 1, 10);
    private static final Armor TEST_ARMOR = new Armor("Test Armor", 1, 10, 10);
    private static final Spell TEST_SPELL = new Spell("Test Spell", 1, 20, 50);
    private static final Drinkable TEST_HEALING_POTION = new HealingPotion("Test Potion", 30);
    private static final Drinkable TEST_MANA_POTION = new ManaPotion("Test Potion", 30);

    private Hero hero;

    @Before
    public void setup() {
        hero = new Hero("Test Hero");
    }

    @Test
    public void testTakeHealingNormalCase() {
        int damageTaken = hero.takeDamage(50);
        int healingPoints = damageTaken - 10;
        hero.takeHealing(healingPoints);

        int expected = 90;
        int result = hero.getStats().getHealth();

        assertEquals("take healing normal case", expected, result);
    }

    @Test
    public void testTakeHealingShouldNotGoOverMaxHealth() {
        hero.takeDamage(10);
        hero.takeHealing(100);

        int expected = 100;
        int result = hero.getStats().getHealth();

        assertEquals("takeHealing should not go over max hp", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeHealingIfHealingPointsAreNegative() {
        hero.takeHealing(-20);
    }

    @Test
    public void testTakeManaNormalCase() {
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);
        hero.attack();
        hero.takeMana(20);

        int expected = 70;
        int result = hero.getStats().getMana();

        assertEquals("takeMana normal case", expected, result);
    }

    @Test
    public void testTakeManaShouldNotGoOverMaxMana() {
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);
        hero.attack();
        hero.takeMana(200);

        int expected = 100;
        int result = hero.getStats().getMana();

        assertEquals("takeMana should not go over mana cap", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeManaIfManaPointsAreNegative() {
        hero.takeMana(-50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEquipIfNewWeaponIsNull() {
        Weapon weapon = null;
        hero.equip(weapon);
    }

    @Test
    public void testEquipIfNewWeaponIsHigherLevel() {
        Weapon higherLevelWeapon = new Weapon("Great Sword", 5, 40);
        hero.addToBackpack(higherLevelWeapon);

        assertFalse("equip if weapon is higher level", hero.equip(higherLevelWeapon));
    }

    @Test(expected = InvalidItemException.class)
    public void testEquipWithInvalidWeapon() {
        hero.equip(TEST_WEAPON);
    }

    @Test
    public void testEquipWeaponShouldChangeWeapon() {
        Weapon oldWeapon = hero.getWeapon();

        hero.addToBackpack(TEST_WEAPON);
        hero.equip(TEST_WEAPON);

        Weapon newWeapon = hero.getWeapon();

        assertNotEquals("equip should change weapon", oldWeapon, newWeapon);
        assertEquals("new weapon should be equipped one", TEST_WEAPON, newWeapon);
    }

    @Test
    public void testEquipIfPutsOldWeaponInBackpack() {
        Weapon oldWeapon = hero.getWeapon();

        hero.addToBackpack(TEST_WEAPON);
        hero.equip(TEST_WEAPON);

        Treasure fromBackpack = hero.getItem(1);

        assertEquals("equip should put old weapon in backpack", oldWeapon, fromBackpack);
    }

    @Test(expected = InvalidItemException.class)
    public void testEquipShouldRemoveNewWeaponFromBackpack() {
        hero.addToBackpack(TEST_WEAPON);
        hero.equip(TEST_WEAPON);

        hero.dropItem(TEST_WEAPON);
    }

    @Test
    public void testEquipWeaponWhenBackpackIsFull() {
        Treasure oldWeapon = hero.getWeapon();

        fillBackpack(TEST_ARMOR);
        hero.dropItem(TEST_ARMOR);
        hero.addToBackpack(TEST_WEAPON);
        hero.equip(TEST_WEAPON);

        Treasure fromBackpack = hero.getItem(1);

        assertEquals("equip weapon with full backpack", oldWeapon, fromBackpack);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEquipIfNewArmorIsNull() {
        Armor armor = null;
        hero.equip(armor);
    }

    @Test
    public void testEquipIfNewArmorIsHigherLevel() {
        Armor higherLevelArmor = new Armor("Great Armor", 2, 20, 20);
        hero.addToBackpack(higherLevelArmor);

        assertFalse("equip if armor is higher level", hero.equip(higherLevelArmor));
    }

    @Test(expected = InvalidItemException.class)
    public void testEquipWithInvalidArmor() {
        hero.equip(TEST_ARMOR);
    }

    @Test
    public void testEquipArmorShouldChangeArmor() {
        Armor oldArmor = hero.getArmor();

        hero.addToBackpack(TEST_ARMOR);
        hero.equip(TEST_ARMOR);

        Treasure newArmor = hero.getArmor();

        assertNotEquals("equip should change armor", oldArmor, newArmor);
        assertEquals("new armor should be equipped one", TEST_ARMOR, newArmor);
    }

    @Test
    public void testEquipShouldPutOldArmorInBackpack() {
        Armor oldArmor = hero.getArmor();

        hero.addToBackpack(TEST_ARMOR);
        hero.equip(TEST_ARMOR);

        assertEquals("equip should put old armor in backpack", oldArmor, hero.getItem(1));
    }

    @Test(expected = InvalidItemException.class)
    public void testEquipShouldRemoveNewArmorFromBackpack() {
        hero.addToBackpack(TEST_ARMOR);
        hero.equip(TEST_ARMOR);

        hero.dropItem(TEST_ARMOR);
    }

    @Test
    public void testEquipArmorWhenBackpackIsFull() {
        Treasure oldArmor = hero.getArmor();

        fillBackpack(TEST_SPELL);
        hero.dropItem(TEST_SPELL);
        hero.addToBackpack(TEST_ARMOR);
        hero.equip(TEST_ARMOR);

        Treasure fromBackpack = hero.getItem(1);

        assertEquals("equip armor with full backpack", oldArmor, fromBackpack);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLearnIfNewSpellIsNull() {
        Spell spell = null;
        hero.learn(spell);
    }

    @Test
    public void testLearnIfNewSpellIsHigherLevel() {
        Spell higherLevelSpell = new Spell("Great Spell", 2, 20, 50);
        hero.addToBackpack(higherLevelSpell);

        assertFalse("learn if spell is higher level", hero.learn(higherLevelSpell));
    }

    @Test(expected = InvalidItemException.class)
    public void testLearnWithInvalidSpell() {
        hero.learn(TEST_SPELL);
    }

    @Test
    public void testLearnShouldChangeSpell() {
        Spell spell = new Spell("Some spell", 1, 40, 30);
        hero.addToBackpack(spell);
        hero.learn(spell);
        Spell oldSpell = hero.getSpell();

        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        Spell newSpell = hero.getSpell();

        assertNotEquals("learn should change spell", oldSpell, newSpell);
        assertEquals("new spell should be learned one", TEST_SPELL, newSpell);
    }

    @Test(expected = InvalidItemException.class)
    public void testLearnShouldNotPutOldSpellInBackpack() {
        Spell spell = new Spell("Some spell", 1, 40, 30);
        hero.addToBackpack(spell);
        hero.learn(spell);
        Spell oldSpell = hero.getSpell();

        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        hero.dropItem(oldSpell);
    }

    @Test(expected = InvalidItemException.class)
    public void testLearnShouldRemoveNewSpellFromBackpack() {
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        hero.dropItem(TEST_SPELL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLearnWhenBackpackIsFull() {
        fillBackpack(TEST_ARMOR);
        hero.dropItem(TEST_ARMOR);
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        hero.getItem(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDrinkIfPotionIsNull() {
        Drinkable potion = null;

        hero.drink(potion);
    }

    @Test(expected = InvalidItemException.class)
    public void testDrinkWithInvalidPotion() {
        hero.drink(TEST_HEALING_POTION);
    }

    @Test
    public void testDrinkWithHealingPotionShouldRestoreHealth() {
        final int damageTaken = hero.takeDamage(50);

        hero.addToBackpack(TEST_HEALING_POTION);
        hero.drink(TEST_HEALING_POTION);

        final int expectedHealth = 100 - damageTaken + 30;
        final int resultHeath = hero.getStats().getHealth();

        assertEquals("health should be expected amount", expectedHealth, resultHeath);
    }

    @Test
    public void testDrinkWithHealingPotionShouldNotGoOverMaxHealth() {
        hero.takeDamage(20);

        hero.addToBackpack(TEST_HEALING_POTION);
        hero.drink(TEST_HEALING_POTION);

        final int expectedHealth = 100;
        final int resultHeath = hero.getStats().getHealth();

        assertEquals("health should be expected amount", expectedHealth, resultHeath);
    }

    @Test
    public void testDrinkWithManaPotionShouldRestoreMana() {
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        hero.attack();

        hero.addToBackpack(TEST_MANA_POTION);
        hero.drink(TEST_MANA_POTION);

        final int spellCost = hero.getSpell().getManaCost();
        final int expectedMana = 100 - spellCost + 30;
        final int resultMana = hero.getStats().getMana();

        assertEquals("mana should be expected amount", expectedMana, resultMana);
    }

    @Test
    public void testDrinkWithManaPotionShouldNotGoOverMaxMana() {
        hero.addToBackpack(TEST_SPELL);
        hero.learn(TEST_SPELL);

        hero.attack();

        ManaPotion strongPotion = new ManaPotion("Test Potion", 120);
        hero.addToBackpack(strongPotion);
        hero.drink(strongPotion);

        final int expectedMana = 100;
        final int resultMana = hero.getStats().getMana();

        assertEquals("mana should not go over max mana", expectedMana, resultMana);
    }

    @Test(expected = InvalidItemException.class)
    public void testDrinkShouldRemovePotionFromBackpack() {
        hero.addToBackpack(TEST_HEALING_POTION);
        hero.drink(TEST_HEALING_POTION);

        hero.dropItem(TEST_HEALING_POTION);
    }

    @Test
    public void testAddToBackpackNormalCase() {
        try {
            hero.getItem(1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue("addToBackpack normal case", hero.addToBackpack(TEST_SPELL));
            Treasure newSpell = hero.getItem(1);

            assertEquals("addToBackpack normal case should have spell", TEST_SPELL, newSpell);
        }
    }

    @Test
    public void testAddToBackpackIfFull() {
        fillBackpack(TEST_ARMOR);

        assertFalse("addToBackpack if full", hero.addToBackpack(TEST_SPELL));

        for (int i = 1; i < 10; i++) {
            if (hero.getItem(i).equals(TEST_SPELL)) {
                fail();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToBackpackIfTreasureIsNull() {
        Weapon weapon = null;
        hero.addToBackpack(weapon);
    }

    @Test
    public void testGainExperienceNormalCase() {
        int prevXp = hero.getExperience();

        hero.gainExperience(200);
        int newXp = hero.getExperience();

        assertEquals("gainExperience normal case", prevXp + 200, newXp);
    }

    @Test
    public void testGainExperienceIfLevelsUp() {
        int levelsGained = hero.gainExperience(1500);

        assertTrue("gainExperience if levels up", levelsGained > 0);
    }

    @Test
    public void testGainExperienceCouldNotLevelUp() {
        int levelsGained = hero.gainExperience(100);

        assertEquals("gainExperience could not level up", 0, levelsGained);
    }

    @Test
    public void testGainExperienceShouldNotGoOverCap() {
        hero.gainExperience(20000);

        assertEquals("gainExperience should not go over xp cap", 15000, hero.getExperience());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGainExperienceIfXpPointsAreNegative() {
        hero.gainExperience(-700);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDropItemIfTreasureIsNull() {
        Treasure t = null;
        hero.dropItem(t);
    }

    @Test(expected = InvalidItemException.class)
    public void testDropItemWithInvalidItem() {
        hero.dropItem(TEST_SPELL);
    }

    @Test(expected = InvalidItemException.class)
    public void testDropItemShouldRemoveItemFromBackpack() {
        hero.addToBackpack(TEST_ARMOR);
        hero.dropItem(TEST_ARMOR);
        hero.dropItem(TEST_ARMOR);
    }

    @Test
    public void testGetCharacterInfo() {
        String expected = """
                Hero name: Test Hero
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

        String result = hero.getCharacterInfo();

        assertEquals("getCharacterInfo", expected, result);
    }

    @Test
    public void testGetBackpackInfoIfEmpty() {
        String expected = "Backpack is empty." + System.lineSeparator();
        String result = hero.getBackpackInfo();

        assertEquals("getBackPackInfo if empty", expected, result);
    }

    @Test
    public void testGetBackpackInfoWithItemsInBackpack() {
        hero.addToBackpack(TEST_WEAPON);
        hero.addToBackpack(TEST_ARMOR);
        hero.addToBackpack(TEST_SPELL);

        String expected = "Backpack contents:\n";
        expected += "  1. Test Weapon\n";
        expected += "  2. Test Armor\n";
        expected += "  3. Test Spell\n";

        String result = hero.getBackpackInfo();

        assertEquals("getBackpackInfo", expected, result);
    }

    private void fillBackpack(Treasure t) {
        for (int i = 0; i < 10; i++) {
            hero.addToBackpack(t);
        }
    }
}
