package bg.sofia.uni.fmi.mjt.dungeon.treasure;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;

public interface Treasure {

    default String collect(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Given hero in collect method is null.\n");
        }

        if (hero.addToBackpack(this)) {
            return getName() + " collected successfully in backpack." + System.lineSeparator();
        }

        return "Your backpack is full!" + System.lineSeparator();
    }

    String getName();

    int getLevel();

    TreasureType getType();

    String getInfo();

    boolean use(Hero hero);
}
