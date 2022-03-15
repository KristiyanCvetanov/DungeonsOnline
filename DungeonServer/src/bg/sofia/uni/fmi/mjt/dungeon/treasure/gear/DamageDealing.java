package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import java.util.Objects;

public abstract class DamageDealing extends Gear {

    private final int damage;

    public DamageDealing(String name, int level, int damage) {
        super(name, level);
        this.damage = damage;
    }

    @Override
    public int getPower() {
        final int damagePowerIndex = 10;
        return damage * damagePowerIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DamageDealing that = (DamageDealing) o;
        return damage == that.damage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), damage);
    }

    public int getDamage() {
        return damage;
    }
}
