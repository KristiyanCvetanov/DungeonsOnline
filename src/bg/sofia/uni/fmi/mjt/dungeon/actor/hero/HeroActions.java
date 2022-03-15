package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.Drinkable;

public interface HeroActions {

    void takeHealing(int healingPoints);

    void takeMana(int manaPoints);

    boolean equip(Weapon newWeapon);

    boolean equip(Armor newArmor);

    boolean learn(Spell newSpell);

    boolean drink(Drinkable potion);

    boolean addToBackpack(Treasure treasure);

    int gainExperience(int xpPoints);

    Treasure getItem(int index);

    void dropItem(Treasure treasure);

    String getCharacterInfo();

    String getBackpackInfo();

    int getExperience();
}
