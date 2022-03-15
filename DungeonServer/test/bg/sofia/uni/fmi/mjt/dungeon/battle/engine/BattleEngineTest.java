package bg.sofia.uni.fmi.mjt.dungeon.battle.engine;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BattleEngineTest {

    private BattleEngine battleEngine;

    @BeforeClass
    public static void setBattleEngine() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBattleEngineCannotBeCreatedWithIdenticalActors() {
        Hero hero = new Hero("Test Hero");

        battleEngine = new BattleEngine(hero, hero);
    }

    @Test
    public void testPerformShouldBeADeathMatch() {
        Hero hero1 = new Hero("Test Hero1");
        Hero hero2 = new Hero("Test Hero2");

        battleEngine = new BattleEngine(hero1, hero2);
        battleEngine.perform();
        boolean exactlyOneIsDead = hero1.isAlive() ^ hero2.isAlive();

        assertTrue("perform should be a death match", exactlyOneIsDead);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPerformCanBeInvokedOnlyOnce() {
        Hero hero1 = new Hero("Test Hero1");
        Hero hero2 = new Hero("Test Hero2");

        battleEngine = new BattleEngine(hero1, hero2);
        battleEngine.perform();
        battleEngine.perform();
    }

    @Test
    public void testPerformStrongerShouldBeatWeaker() {
        Hero stronger = new Hero("Stronger Hero");
        Hero weaker = new Hero("Weaker Hero");

        Weapon weapon = new Weapon("Test Weapon", 1, 50);
        Armor armor = new Armor("Test Armor", 1, 100, 200);
        stronger.addToBackpack(weapon);
        stronger.addToBackpack(armor);
        stronger.equip(weapon);
        stronger.equip(armor);

        battleEngine = new BattleEngine(weaker, stronger);
        battleEngine.perform();

        boolean strongerBeatWeaker = stronger.isAlive() && !weaker.isAlive();

        assertTrue("perform stronger should beat weaker", strongerBeatWeaker);
    }

    @Test
    public void testGetCommentaryBeforePerform() {
        Hero hero1 = new Hero("Test Hero1");
        Hero hero2 = new Hero("Test Hero2");

        battleEngine = new BattleEngine(hero1, hero2);

        String expected = "The battle has not been performed yet.\n";
        String result = battleEngine.getCommentary();

        assertEquals("getCommentary before perform", expected, result);
    }

    @Test
    public void testGetCommentaryAfterPerform() {
        Hero hero1 = new Hero("Test Hero1");
        Hero hero2 = new Hero("Test Hero2");

        battleEngine = new BattleEngine(hero1, hero2);
        battleEngine.perform();

        final String commentaryPrefix = "A battle between Test Hero1 and Test Hero2 has begun!\n";
        final String commentarySuffix = """
                Battle has ended!
                Winner is Test Hero1.
                Defeated is Test Hero2.
                """;

        String commentary = battleEngine.getCommentary();

        assertTrue("getCommentary after perform prefix", commentary.startsWith(commentaryPrefix));
        assertTrue("getCommentary after perform suffix", commentary.endsWith(commentarySuffix));
    }
}
