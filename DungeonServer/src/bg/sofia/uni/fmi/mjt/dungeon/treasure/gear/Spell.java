package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.util.Objects;

public class Spell extends DamageDealing {

    private final int manaCost;

    public Spell(String name, int level, int damage, int manaCost) {
        super(name, level, damage);
        this.manaCost = manaCost;
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public TreasureType getType() {
        return TreasureType.LEARNABLE;
    }

    @Override
    public boolean use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Given hero in use method is null.\n");
        }

        return hero.learn(this);
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
        Spell spell = (Spell) o;
        return manaCost == spell.manaCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), manaCost);
    }

    @Override
    public String getInfo() {
        String info;

        info = "    Treasure type: Spell\n";
        info += ("    Name: " + getName() + "\n");
        info += ("    Level: " + getLevel() + "\n");
        info += ("    Damage: " + getDamage() + "\n");
        info += ("    Mana cost: " + manaCost + "\n");

        return info;
    }
}
