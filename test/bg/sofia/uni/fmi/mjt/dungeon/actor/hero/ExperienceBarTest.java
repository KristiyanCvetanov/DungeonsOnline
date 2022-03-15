package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExperienceBarTest {

    private static ExperienceBar xpBar;

    @BeforeClass
    public static void setup() {
        xpBar = ExperienceBar.getInstance();
    }

    @Test
    public void testLevelUpWithXpForLevelOne() {
        for (int i = 0; i < 1000; i++) {
            assertEquals("levelUp for level one", 1, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelTwo() {
        for (int i = 1000; i < 2200; i++) {
            assertEquals("levelUp for level two", 2, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelThree() {
        for (int i = 2200; i < 3500; i++) {
            assertEquals("levelUp for level three", 3, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelFour() {
        for (int i = 3500; i < 5000; i++) {
            assertEquals("levelUp for level four", 4, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelFive() {
        for (int i = 5000; i < 6800; i++) {
            assertEquals("levelUp for level one", 5, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelSix() {
        for (int i = 6800; i < 8500; i++) {
            assertEquals("levelUp for level six", 6, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelSeven() {
        for (int i = 8500; i < 10500; i++) {
            assertEquals("levelUp for level seven", 7, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelEight() {
        for (int i = 10500; i < 12700; i++) {
            assertEquals("levelUp for level eight", 8, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelNine() {
        for (int i = 12700; i < 15000; i++) {
            assertEquals("levelUp for level nine", 9, xpBar.levelUp(i));
        }
    }

    @Test
    public void testLevelUpWithXpForLevelTen() {
        assertEquals("levelUp for level ten", 10, xpBar.levelUp(15000));
    }

    @Test
    public void testLevelUpWithXpOverXpCap() {
        assertEquals("levelUp with xp over xp cap", 10, xpBar.levelUp(20000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLevelUpWithNegativeXp() {
        xpBar.levelUp(-1000);
    }

    @Test
    public void testNeededXpForNextLevelOnLevelOne() {
        for (int i = 0; i < 1000; i++) {
            assertEquals("neededXpForNextLevel level on level one",
                    1000 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelTwo() {
        for (int i = 1000; i < 2200; i++) {
            assertEquals("neededXpForNextLevel on level two",
                    2200 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelThree() {
        for (int i = 2200; i < 3500; i++) {
            assertEquals("neededXpForNextLevel on level three",
                    3500 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelFour() {
        for (int i = 3500; i < 5000; i++) {
            assertEquals("neededXpForNextLevel on level four",
                    5000 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelFive() {
        for (int i = 5000; i < 6800; i++) {
            assertEquals("neededXpForNextLevel on level five",
                    6800 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelSix() {
        for (int i = 6800; i < 8500; i++) {
            assertEquals("neededXpForNextLevel on level six",
                    8500 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelSeven() {
        for (int i = 8500; i < 10500; i++) {
            assertEquals("neededXpForNextLevel on level seven",
                    10500 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelEight() {
        for (int i = 10500; i < 12700; i++) {
            assertEquals("neededXpForNextLevel on level eight",
                    12700 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelNine() {
        for (int i = 12700; i < 15000; i++) {
            assertEquals("neededXpForNextLevel on level nine",
                    15000 - i,
                    xpBar.neededXpForNextLevel(i));
        }
    }

    @Test
    public void testNeededXpForNextLevelOnLevelTen() {
        assertEquals("neededXpForNextLevel on level ten",
                0,
                xpBar.neededXpForNextLevel(15000));

    }

    @Test
    public void testNeededXpForNextLevelOverXpCap() {
        assertEquals("neededXpForNextLevel with xp over xp cap",
                0,
                xpBar.neededXpForNextLevel(18000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNeededXpForNextLevelWithNegativeXp() {
        xpBar.neededXpForNextLevel(-4000);
    }
}
