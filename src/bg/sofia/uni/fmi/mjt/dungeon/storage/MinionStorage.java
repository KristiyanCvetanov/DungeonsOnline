package bg.sofia.uni.fmi.mjt.dungeon.storage;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Stats;
import bg.sofia.uni.fmi.mjt.dungeon.actor.minion.Minion;
import bg.sofia.uni.fmi.mjt.dungeon.exception.StorageInitializationException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public final class MinionStorage {

    private static MinionStorage instance = null;

    private final TreasureStorage treasures;
    private final Map<Integer, List<Minion>> minions;

    {
        minions = new HashMap<>();

        final int maxLevel = 10;
        for (int i = 1; i <= maxLevel; i++) {
            List<Minion> currLevelMinions = new ArrayList<>();
            minions.put(i, currLevelMinions);
        }
    }

    private MinionStorage(Reader treasureReader, Reader minionReader) {
        treasures = TreasureStorage.getInstance(treasureReader);
        initializeMinions(minionReader);
    }

    public static MinionStorage getInstance(Reader treasureReader, Reader minionReader) {
        if (instance == null) {
            instance = new MinionStorage(treasureReader, minionReader);
        }

        return instance;
    }

    public List<Minion> getMinionsByLevel(int level) {
        return Collections.unmodifiableList(minions.get(level));
    }

    private void initializeMinions(Reader minionsReader) {
        try {
            var buffReader = new BufferedReader(minionsReader);
            String line;

            while ((line = buffReader.readLine()) != null) {
                Minion currMinion = loadMinion(line);

                minions.get(currMinion.getLevel()).add(currMinion);
            }
        } catch (IOException e) {
            throw new StorageInitializationException("There is a problem with loading minions from file.\n", e);
        }
    }

    private Minion loadMinion(String currMinion) {
        final String regex = ", ";
        String[] minionComponents = currMinion.split(regex);

        String name = loadMinionName(minionComponents);
        int level = loadMinionLevel(minionComponents);
        Weapon weapon = loadMinionWeapon(minionComponents);
        Armor armor = loadMinionArmor(minionComponents);
        Spell spell = loadMinionSpell(minionComponents);
        Stats stats = loadMinionStats(minionComponents, armor);

        return new Minion(name, level, stats, weapon, armor, spell);
    }

    private String loadMinionName(String[] components) {
        final int nameIndex = 0;

        return components[nameIndex];
    }

    private Stats loadMinionStats(String[] components, Armor armor) {
        final int healthIndex = 1;
        final int manaIndex = 2;
        final int attackIndex = 3;
        final int defenceIndex = 4;

        final int health = Integer.parseInt(components[healthIndex]);
        final int mana = Integer.parseInt(components[manaIndex]);
        final int attack = Integer.parseInt(components[attackIndex]);
        final int defence = Integer.parseInt(components[defenceIndex]);

        return new Stats(health, armor.getHealth(), mana, 0, attack, defence);
    }

    private Weapon loadMinionWeapon(String[] components) {
        final int weaponIndexFromFile = 5;
        final int weaponIndexFromStorage = Integer.parseInt(components[weaponIndexFromFile]);

        if (weaponIndexFromStorage < 0) {
            return new Weapon();
        }

        return treasures.getWeapon(weaponIndexFromStorage);
    }

    private Armor loadMinionArmor(String[] components) {
        final int armorIndexFromFile = 6;
        final int armorIndexFromStorage = Integer.parseInt(components[armorIndexFromFile]);

        if (armorIndexFromStorage < 0) {
            return new Armor();
        }

        return treasures.getArmor(armorIndexFromStorage);
    }

    private Spell loadMinionSpell(String[] components) {
        final int spellIndexFromFile = 7;
        final int spellIndexFromStorage = Integer.parseInt(components[spellIndexFromFile]);

        if (spellIndexFromStorage < 0) {
            return null;
        }

        return treasures.getSpell(spellIndexFromStorage);
    }

    private int loadMinionLevel(String[] components) {
        final int levelIndex = 8;

        return Integer.parseInt(components[levelIndex]);
    }
}
