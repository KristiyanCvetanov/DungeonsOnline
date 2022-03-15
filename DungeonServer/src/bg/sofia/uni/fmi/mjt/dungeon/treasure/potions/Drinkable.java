package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

public interface Drinkable extends Treasure {

    void heal(Hero hero);
}
