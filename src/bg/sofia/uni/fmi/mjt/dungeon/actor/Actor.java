package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;

public interface Actor {

    String getName();

    boolean isAlive();

    int takeDamage(int damagePoints);

    int attack();

    Weapon getWeapon();

    Armor getArmor();

    Spell getSpell();
}
