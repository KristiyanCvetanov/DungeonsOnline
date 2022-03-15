package bg.sofia.uni.fmi.mjt.dungeon.actor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatsTest {

    private Stats stats;

    @Before
    public void setup() {
        stats = new Stats();
    }

    @Test
    public void testLevelUpStatsShouldIncreaseMaxStats() {
        final int prevHealth = stats.getHealth();
        final int prevMana = stats.getMana();
        final int prevAttack = stats.getAttack();
        final int prevDefence = stats.getDefense();

        stats.levelUpStats();

        assertEquals("levelUp increases max health", prevHealth + 10, stats.getHealth());
        assertEquals("levelUp increases max mana", prevMana + 10, stats.getMana());
        assertEquals("levelUp increases attack", prevAttack + 5, stats.getAttack());
        assertEquals("levelUp increases defence", prevDefence + 5, stats.getDefense());
    }

    @Test
    public void testLevelUpStatsShouldRestoreHealthAndMana() {
        stats.extractFromHealth(20);
        stats.extractFromMana(20);

        stats.levelUpStats();

        final int expectedHealth = 110;
        final int expectedMana = 110;

        assertEquals("levelUp restores health", expectedHealth, stats.getHealth());
        assertEquals("levelUp restores mana", expectedMana, stats.getMana());
    }

    @Test
    public void testAddToHealthNormalCase() {
        stats.extractFromHealth(50);

        stats.addToHealth(30);
        final int expectedHealth = 80;

        assertEquals("addToHealth normal case", expectedHealth, stats.getHealth());
    }

    @Test
    public void testAddToHealthShouldNotGoOverMaxHealth() {
        stats.extractFromHealth(30);

        stats.addToHealth(50);
        final int expectedHealth = 100;

        assertEquals("addToHealth should cap at max health", expectedHealth, stats.getHealth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToHealthIfArgumentIsNegative() {
        stats.addToHealth(-20);
    }

    @Test
    public void testAddToManaNormalCase() {
        stats.extractFromMana(40);

        stats.addToMana(20);
        final int expectedMana = 80;

        assertEquals("addToMana normal case", expectedMana, stats.getMana());
    }

    @Test
    public void testAddToManaShouldNotGoOverMaxMana() {
        stats.extractFromMana(30);

        stats.addToMana(50);
        final int expectedMana = 100;

        assertEquals("addToMana should cap at max mana", expectedMana, stats.getMana());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToManaIfArgumentIsNegative() {
        stats.addToMana(-20);
    }

    @Test
    public void testExtractFromHealthNormalCase() {
        stats.extractFromHealth(50);
        final int expectedHealth = 50;

        assertEquals("extractFromHealth normal case", expectedHealth, stats.getHealth());
    }

    @Test
    public void testExtractFromHealthShouldNotGoUnderZero() {
        stats.extractFromHealth(110);

        assertEquals("extractFromHealth should not go under zero", 0, stats.getHealth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFromHealthIfArgumentIsNegative() {
        stats.extractFromHealth(-30);
    }

    @Test
    public void testExtractFromManaNormalCase() {
        stats.extractFromMana(50);
        final int expectedMana = 50;

        assertEquals("extractFromMana normal case", expectedMana, stats.getMana());
    }

    @Test
    public void testExtractFromManaShouldNotGoUnderZero() {
        stats.extractFromMana(110);

        assertEquals("extractFromMana should not go under zero", 0, stats.getMana());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFromManaIfArgumentIsNegative() {
        stats.extractFromMana(-30);
    }
}
