package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.util.Objects;

public abstract class AbstractPotion implements Drinkable {

    private final String name;
    private final int level;
    protected final int restorationPoints;

    public AbstractPotion(String name, int restorationPoints) {
        this.name = name;
        this.level = 1;  // every potion should be accessible
        this.restorationPoints = restorationPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractPotion that = (AbstractPotion) o;
        return level == that.level
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public TreasureType getType() {
        return TreasureType.DRINKABLE;
    }

    @Override
    public boolean use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Given hero in use method is null.\n");
        }

        hero.drink(this);

        return true;
    }
}
