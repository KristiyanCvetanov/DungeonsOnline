package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.util.Objects;

public abstract class Gear implements Treasure {

    private final String name;
    private final int level;

    public Gear(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Gear gear = (Gear) o;
        return level == gear.level
                && Objects.equals(name, gear.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level);
    }

    @Override
    public int getLevel() {
        return level;
    }

    public abstract int getPower();
}
