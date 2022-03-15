package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class AbstractActorTest {

    private Hero actor;  // To equip/learn gear, we need the Hero class.
    // Only methods implemented in AbstractActor will be tested.

    @Before
    public void setup() {
        actor = new Hero("Kris");
    }

    @Test
    public void testIsAliveWhenTakingNonLethalDamage() {
        actor.takeDamage(10);

        assertTrue("isAlive when taking non-lethal damage", actor.isAlive());
    }

    @Test
    public void testIsAliveWhenTakingLethalDamage() {
        actor.takeDamage(1000);

        assertFalse("isAlive when taking non-lethal damage", actor.isAlive());
    }

    @Test
    public void testTakeDamageNormalCase() {
        final int expected = 90;
        final int result = actor.takeDamage(100);

        assertEquals("takeDamage normal case", expected, result);
    }

    @Test
    public void testTakeDamageIfArgumentIsZero() {
        final int expected = 0;
        final int result = actor.takeDamage(0);

        assertEquals("takeDamage when taking no damage", expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeDamageIfArgumentIsNegative() {
        actor.takeDamage(-20);
    }

    @Test
    public void testAttackWhenSpellIsStronger() {
        Spell spell = new Spell("Good spell", 1, 20, 10);
        actor.addToBackpack(spell);
        spell.use(actor);

        final int actorRawAttack = 50;
        final int spellAttack = spell.getDamage();
        final int expected = actorRawAttack + spellAttack;

        final int result = actor.attack();

        assertEquals("attack when spell is stronger", expected, result);
    }

    @Test
    public void testAttackWhenWeaponIsStronger() {
        Spell spell = new Spell("Good Spell", 1, 20, 10);
        actor.addToBackpack(spell);
        spell.use(actor);

        Weapon weapon = new Weapon("Even Better Weapon", 1, 50);
        actor.addToBackpack(weapon);
        weapon.use(actor);

        final int actorRawAttack = 50;
        final int weaponAttack = weapon.getDamage();
        final int expected = actorRawAttack + weaponAttack;

        final int result = actor.attack();

        assertEquals("attack when weapon is stronger", expected, result);
    }

    @Test
    public void testAttackWhenSpellIsNull() {
        final int expected = 50;

        final int result = actor.attack();

        assertEquals("attack when spell is null", expected, result);
    }

    @Test
    public void testAttackShouldTakeManaWhenUsingSpell() {
        Spell spell = new Spell("Good Spell", 1, 20, 10);
        actor.addToBackpack(spell);
        spell.use(actor);

        actor.attack();

        final int expected = 90;
        final int result = actor.getStats().getMana();

        assertEquals("attack should take mana when using spell", expected, result);
    }

    @Test
    public void testAttackWhenThereIsNotEnoughMana() {
        Spell spell = new Spell("Good Spell", 1, 20, 150);
        actor.addToBackpack(spell);
        spell.use(actor);

        Weapon weapon = new Weapon("Worse Weapon", 1, 10);
        actor.addToBackpack(weapon);
        weapon.use(actor);

        final int actorRawAttack = 50;
        final int expected = actorRawAttack + weapon.getDamage();
        final int result = actor.attack();

        assertEquals("attack when there is not enough mana", expected, result);
    }

    @Test
    public void testGetStatsInfo() {
        String expected = "    health: 100\n"
                + "    mana: 100\n"
                + "    attack: 50\n"
                + "    defence: 50\n";

        String result = actor.getStatsInfo();

        assertEquals("getStatsInfo", expected, result);
    }

    @Test
    public void testGetExperienceWorthNormalCase() {
        final int expected = 1550;
        final int result = actor.getExperienceWorth();

        assertEquals("getExperienceWorth normal case", expected, result);
    }

    @Test
    public void testGetExperienceWorthWhenSpellIsNull() {
        Spell spell = new Spell("Good Spell", 1, 20, 150);
        actor.addToBackpack(spell);
        spell.use(actor);

        final int expected = 1750;
        final int result = actor.getExperienceWorth();

        assertEquals("getExperienceWorth with stronger spell", expected, result);
    }
}
