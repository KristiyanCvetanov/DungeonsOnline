package bg.sofia.uni.fmi.mjt.dungeon.storage;

import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.StorageInitializationException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.Drinkable;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.HealingPotion;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.ManaPotion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public final class TreasureStorage {

    private static final String REGEX = ", ";
    private static TreasureStorage instance = null;

    private final Map<Integer, Set<Integer>> treasuresByLevel;

    private Map<Integer, Weapon> weapons;
    private Map<Integer, Spell> spells;
    private Map<Integer, Armor> armors;
    private Map<Integer, Drinkable> potions;

    {
        treasuresByLevel = new HashMap<>();

        final int maxLevel = 10;
        for (int i = 1; i <= maxLevel; i++) {
            Set<Integer> currLevelTreasures = new HashSet<>();
            treasuresByLevel.put(i, currLevelTreasures);
        }
    }

    private TreasureStorage(Reader treasureReader) {
        initializeStorage(treasureReader);
    }

    public static TreasureStorage getInstance(Reader treasureReader) {
        if (instance == null) {
            instance = new TreasureStorage(treasureReader);
        }

        return instance;
    }

    public Set<Treasure> getTreasuresByLevel(int level) {
        Set<Treasure> wantedLevelTreasures = new HashSet<>();
        Set<Integer> treasureIndices = treasuresByLevel.get(level);

        if (treasureIndices != null) {
            for (int i : treasureIndices) {
                wantedLevelTreasures.add(getTreasureByIndex(i));
            }
        }

        return wantedLevelTreasures;
    }

    public Weapon getWeapon(int weaponIndex) {
        if (!weapons.containsKey(weaponIndex)) {
            throw new InvalidItemException("There is no such weapon in the treasure storage.");
        }

        return weapons.get(weaponIndex);
    }

    public Spell getSpell(int spellIndex) {
        if (!spells.containsKey(spellIndex)) {
            throw new InvalidItemException("There is no such spell in the treasure storage.");
        }

        return spells.get(spellIndex);
    }

    public Armor getArmor(int armorIndex) {
        if (!armors.containsKey(armorIndex)) {
            throw new InvalidItemException("There is no such armor in the treasure storage.");
        }

        return armors.get(armorIndex);
    }

    private void initializeStorage(Reader treasureReader) {
        try (var buffReader = new BufferedReader(treasureReader)) {
            int shift = 0;

            int numOfWeapons = Integer.parseInt(buffReader.readLine());
            weapons = generateWeapons(buffReader, shift, numOfWeapons);
            shift += numOfWeapons;

            int numOfSpells = Integer.parseInt(buffReader.readLine());
            spells = generateSpells(buffReader, shift, numOfSpells);
            shift += numOfSpells;

            int numOfArmors = Integer.parseInt(buffReader.readLine());
            armors = generateArmors(buffReader, shift, numOfArmors);
            shift += numOfArmors;

            potions = generatePotions(buffReader, shift);
        } catch (IOException e) {
            throw new StorageInitializationException("There is a problem with loading treasures from file.\n", e);
        }
    }

    private Map<Integer, Weapon> generateWeapons(BufferedReader buffReader, int shift, int numOfWeapons)
            throws IOException {
        Map<Integer, Weapon> weaponsToLoad = new HashMap<>();

        for (int i = shift; i < numOfWeapons + shift; i++) {
            String[] weaponComponents = buffReader.readLine().split(REGEX);

            String name = loadTreasureName(weaponComponents);
            int level = loadTreasureLevel(weaponComponents);
            int attack = loadTreasureAttack(weaponComponents);

            weaponsToLoad.put(i, new Weapon(name, level, attack));
            treasuresByLevel.get(level).add(i);
        }

        return weaponsToLoad;
    }

    private String loadTreasureName(String[] components) {
        final int nameIndex = 0;

        return components[nameIndex];
    }

    private int loadTreasureLevel(String[] components) {
        final int levelIndex = 1;

        return Integer.parseInt(components[levelIndex]);
    }

    private int loadTreasureAttack(String[] components) {
        final int attackIndex = 2;

        return Integer.parseInt(components[attackIndex]);
    }

    private Map<Integer, Spell> generateSpells(BufferedReader buffReader, int shift, int numOfSpells)
            throws IOException {
        Map<Integer, Spell> spellsToLoad = new HashMap<>();

        for (int i = shift; i < numOfSpells + shift; i++) {
            String[] spellComponents = buffReader.readLine().split(REGEX);

            String spellName = loadTreasureName(spellComponents);
            int spellLevel = loadTreasureLevel(spellComponents);
            int spellAttack = loadTreasureAttack(spellComponents);
            int spellManaCost = loadSpellManaCost(spellComponents);

            spellsToLoad.put(i, new Spell(spellName, spellLevel, spellAttack, spellManaCost));
            treasuresByLevel.get(spellLevel).add(i);
        }

        return spellsToLoad;
    }

    private int loadSpellManaCost(String[] components) {
        final int manaCostIndex = 3;

        return Integer.parseInt(components[manaCostIndex]);
    }

    private Map<Integer, Armor> generateArmors(BufferedReader buffReader, int shift, int numOfArmors)
            throws IOException {
        Map<Integer, Armor> armorsToLoad = new HashMap<>();

        for (int i = shift; i < shift + numOfArmors; i++) {
            String[] armorComponents = buffReader.readLine().split(REGEX);

            String armorName = loadTreasureName(armorComponents);
            int armorLevel = loadTreasureLevel(armorComponents);
            int armorDefence = loadArmorDefence(armorComponents);
            int armorHealth = loadArmorHealth(armorComponents);

            armorsToLoad.put(i, new Armor(armorName, armorLevel, armorDefence, armorHealth));
            treasuresByLevel.get(armorLevel).add(i);
        }

        return armorsToLoad;
    }

    private int loadArmorDefence(String[] components) {
        final int defenceIndex = 2;

        return Integer.parseInt(components[defenceIndex]);
    }

    private int loadArmorHealth(String[] components) {
        final int healthIndex = 3;

        return Integer.parseInt(components[healthIndex]);
    }

    private Map<Integer, Drinkable> generatePotions(BufferedReader buffReader, int shift)
            throws IOException {
        Map<Integer, Drinkable> potionsToLoad = new HashMap<>();

        int numOfHealingPotions = Integer.parseInt(buffReader.readLine());
        Map<Integer, HealingPotion> healingPotions = loadHealingPotions(buffReader, shift, numOfHealingPotions);
        shift += numOfHealingPotions;

        int numOfManaPotions = Integer.parseInt(buffReader.readLine());
        Map<Integer, ManaPotion> manaPotions = loadManaPotions(buffReader, shift, numOfManaPotions);

        potionsToLoad.putAll(healingPotions);
        potionsToLoad.putAll(manaPotions);

        return potionsToLoad;
    }

    private Map<Integer, HealingPotion> loadHealingPotions(BufferedReader buffReader, int shift, int num)
            throws IOException {
        Map<Integer, HealingPotion> healingPotions = new HashMap<>();

        for (int i = shift; i < shift + num; i++) {
            String[] potionComponents = buffReader.readLine().split(REGEX);

            String potionName = loadTreasureName(potionComponents);
            int potionHealing = loadPotionRestoring(potionComponents);

            healingPotions.put(i, new HealingPotion(potionName, potionHealing));
            final int potionLevel = 1;
            treasuresByLevel.get(potionLevel).add(i);
        }

        return healingPotions;
    }

    private Map<Integer, ManaPotion> loadManaPotions(BufferedReader buffReader, int shift, int num)
            throws IOException {
        Map<Integer, ManaPotion> manaPotions = new HashMap<>();

        for (int i = shift; i < shift + num; i++) {
            String[] potionComponents = buffReader.readLine().split(REGEX);

            String potionName = loadTreasureName(potionComponents);
            int potionHealing = loadPotionRestoring(potionComponents);

            manaPotions.put(i, new ManaPotion(potionName, potionHealing));
            final int potionLevel = 1;
            treasuresByLevel.get(potionLevel).add(i);
        }

        return manaPotions;
    }

    private int loadPotionRestoring(String[] components) {
        final int restoringIndex = 1;

        return Integer.parseInt(components[restoringIndex]);
    }

    private Treasure getTreasureByIndex(int index) {
        if (weapons.containsKey(index)) {
            return weapons.get(index);
        } else if (spells.containsKey(index)) {
            return spells.get(index);
        } else if (armors.containsKey(index)) {
            return armors.get(index);
        } else if (potions.containsKey(index)) {
            return potions.get(index);
        } else {
            throw new InvalidItemException("There is no such treasure in the storage.\n");
        }
    }
}
