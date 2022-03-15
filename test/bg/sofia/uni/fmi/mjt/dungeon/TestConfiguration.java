package bg.sofia.uni.fmi.mjt.dungeon;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.map.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TestConfiguration {

    private TestConfiguration() {
    }

    public static SocketAddress getSingleClientAddress() {
        byte[] ipBytes = "/127.0.0.1:58912".getBytes();

        try {
            return new InetSocketAddress(InetAddress.getByAddress(ipBytes), 7777);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host used to create socket address.\n", e);
        }
    }

    public static List<SocketAddress> getMultipleSocketAddresses() {
        String oldIp = "/127.0.0.1:5891";
        List<SocketAddress> addresses = new ArrayList<>();
        final int numOfAddresses = 5;
        try {
            // creates addresses /127.0.0.1:58911 to /127.0.0.1:58915
            for (int i = 1; i <= numOfAddresses; i++) {
                String ip = oldIp + i;
                byte[] rawIp = ip.getBytes();
                addresses.add(new InetSocketAddress(InetAddress.getByAddress(rawIp), 7777));
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host for socket address.");
            e.printStackTrace();
        }
        return addresses;
    }

    public static String generateEmptyMap() {
        return """
                7
                4
                0
                0
                .##....
                #....#.
                #....#.
                #..#...""";
    }

    public static String generateMapWithoutObstacles() {
        return """
                7
                4
                0
                0
                .......
                .......
                .......
                .......""";
    }

    public static Hero getTestHero() {
        Hero hero = new Hero("Test Hero");

        Weapon weapon = new Weapon("Test Weapon", 1, 30);
        Spell spell = new Spell("Test Spell", 1, 35, 70);
        hero.addToBackpack(weapon);
        hero.addToBackpack(new HealingPotion("Test Potion", 30));
        hero.addToBackpack(spell);

        hero.equip(weapon);
        hero.learn(spell);
        return hero;
    }

    public static String generateTreasuresFileContent() {
        return generateWeapons()
                + generateSpells()
                + generateArmors()
                + generatePotions();
    }

    public static String generateMinionsFileContent() {
        return generateFirstHalfMinions()
                + generateSecondHalfMinions();
    }

    public static GameState generateGame(String mapStr) {
        StringReader mapReader = new StringReader(mapStr);

        String treasuresInfo = generateTreasuresFileContent();
        StringReader treasureReader = new StringReader(treasuresInfo);

        String minionsInfo = generateMinionsFileContent();
        StringReader minionReader = new StringReader(minionsInfo);

        return new GameState(mapReader, treasureReader, minionReader);
    }

    public static Set<Coordinates> getFreeSpotsOnMap(char[][] map) {
        Set<Coordinates> freeSpots = new HashSet<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '.') {
                    freeSpots.add(new Coordinates(j, i));
                }
            }
        }

        return freeSpots;
    }

    private static String generateWeapons() {
        return """
                10
                Test Weapon1, 1, 10
                Test Weapon2, 2, 20
                Test Weapon3, 3, 30
                Test Weapon4, 4, 40
                Test Weapon5, 5, 50
                Test Weapon6, 6, 60
                Test Weapon7, 7, 70
                Test Weapon8, 8, 80
                Test Weapon9, 9, 90
                Test Weapon10, 10, 100
                """;
    }

    private static String generateSpells() {
        return """
                10
                Test Spell1, 1, 10, 10
                Test Spell2, 2, 20, 20
                Test Spell3, 3, 30, 30
                Test Spell4, 4, 40, 40
                Test Spell5, 5, 50, 50
                Test Spell6, 6, 60, 60
                Test Spell7, 7, 70, 70
                Test Spell8, 8, 80, 80
                Test Spell9, 9, 90, 90
                Test Spell10, 10, 100, 100
                """;
    }

    private static String generateArmors() {
        return """
                10
                Test Armor1, 1, 10, 10
                Test Armor2, 2, 20, 20
                Test Armor3, 3, 30, 30
                Test Armor4, 4, 40, 40
                Test Armor5, 5, 50, 50
                Test Armor6, 6, 60, 60
                Test Armor7, 7, 70, 70
                Test Armor8, 8, 80, 80
                Test Armor9, 9, 90, 90
                Test Armor10, 10, 100, 100
                """;
    }

    private static String generatePotions() {
        return """
                3
                Lesser Healing Potion, 75
                Medium Healing Potion, 150
                Greater Healing Potion, 300
                3
                Lesser Mana Potion, 50
                Medium Mana Potion, 100
                Greater Mana Potion, 150""";
    }

    private static String generateFirstHalfMinions() {
        return """
                Test Minion11, 50, 50, 10, 10, 0, 20, 15, 1
                Test Minion12, 50, 50, 10, 10, 1, 21, 16, 1
                Test Minion13, 50, 50, 10, 10, 2, 22, 17, 1
                Test Minion21, 100, 100, 20, 20, 0, 20, 15, 2
                Test Minion22, 100, 100, 20, 20, 1, 21, 16, 2
                Test Minion23, 100, 100, 20, 20, 2, 22, 17, 2
                Test Minion31, 150, 150, 30, 30, 0, 20, 15, 3
                Test Minion32, 150, 150, 30, 30, 1, 21, 16, 3
                Test Minion33, 150, 150, 30, 30, 2, 22, 17, 3
                Test Minion41, 200, 150, 30, 30, 0, 23, 15, 4
                Test Minion42, 200, 150, 30, 30, 1, 22, 16, 4
                Test Minion43, 200, 150, 30, 30, 2, 22, 17, 4
                Test Minion51, 250, 150, 30, 30, 0, 20, 15, 5
                Test Minion52, 250, 150, 30, 30, 1, 21, 16, 5
                Test Minion53, 250, 150, 30, 30, 2, 22, 17, 5
                """;
    }

    private static String generateSecondHalfMinions() {
        return """
                Test Minion61, 300, 150, 30, 30, 0, 20, 15, 6
                Test Minion62, 300, 150, 30, 30, 1, 21, 16, 6
                Test Minion63, 300, 150, 30, 30, 2, 22, 17, 6
                Test Minion71, 350, 150, 30, 30, 0, 20, 15, 7
                Test Minion72, 350, 150, 30, 30, 1, 21, 16, 7
                Test Minion73, 350, 150, 30, 30, 2, 22, 17, 7
                Test Minion81, 400, 150, 30, 30, 0, 20, 15, 8
                Test Minion82, 400, 150, 30, 30, 1, 21, 16, 8
                Test Minion83, 400, 150, 30, 30, 2, 22, 17, 8
                Test Minion91, 450, 150, 30, 30, 0, 20, 15, 9
                Test Minion92, 450, 150, 30, 30, 1, 21, 16, 9
                Test Minion93, 450, 150, 30, 30, 2, 22, 17, 9
                Test Minion101, 550, 150, 30, 30, 0, 20, 15, 10
                Test Minion102, 550, 150, 30, 30, 1, 21, 16, 10
                Test Minion103, 550, 150, 30, 30, 2, 22, 17, 10""";
    }

    public static String getCommandsInfo() {
        return """
                Supported commands:
                - move up/down/left/right: moves your character in the respective direction
                - character: prints information about your character
                - backpack: prints information about the content in your backpack
                - info <n>: prints information about the item in your backpack with the respective number
                - equip <n>: your character equips the corresponding item from your backpack. Valid
                             only for weapon/armor
                - learn <n>: your character learns the corresponding spell. Valid only for spells
                - drink <n>: your character drinks the corresponding potion. Valid only for potions
                - throw <n>: your character throws the corresponding item
                - give <n>: give another player the corresponding item from your backpack. Valid only
                            if you and the other player are on the same spot on the map.
                - attack: attack another player. Valid only if you and the other player are ont the same
                          spot on the map.
                """;
    }
}
