package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.util.Objects;

public class Armor extends Gear {

    private final int health;
    private final int defence;

    public Armor() {
        super("Old Shirt", 1);
        defence = 0;
        health = 0;
    }

    public Armor(String name, int level, int defence, int health) {
        super(name, level);
        this.defence = defence;
        this.health = health;
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
        Armor armor = (Armor) o;
        return health == armor.health
                && defence == armor.defence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), health, defence);
    }

    @Override
    public TreasureType getType() {
        return TreasureType.EQUIPPABLE;
    }

    @Override
    public boolean use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Given hero in use method is null.\n");
        }

        return hero.equip(this);
    }

    @Override
    public String getInfo() {
        String info;

        info = "    Treasure type: Armor\n";
        info += ("    Name: " + getName() + "\n");
        info += ("    Level: " + getLevel() + "\n");
        info += ("    Defence: " + getDefence() + "\n");
        info += ("    Health: " + getHealth() + "\n");

        return info;
    }


    @Override
    public int getPower() {
        final int healthPowerIndex = 7;
        final int defencePowerIndex = 5;

        return health * healthPowerIndex
                + defence * defencePowerIndex;
    }

    public int getHealth() {
        return health;
    }

    public int getDefence() {
        return defence;
    }
}
