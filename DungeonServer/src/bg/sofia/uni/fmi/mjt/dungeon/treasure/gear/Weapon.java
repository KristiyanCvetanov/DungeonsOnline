package bg.sofia.uni.fmi.mjt.dungeon.treasure.gear;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

public class Weapon extends DamageDealing {

    public Weapon() {
        super("Bare Fists", 1, 0);
    }

    public Weapon(String name, int level, int damage) {
        super(name, level, damage);
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

        info = "    Treasure type: Weapon\n";
        info += ("    Name: " + getName() + "\n");
        info += ("    Level: " + getLevel() + "\n");
        info += ("    Damage: " + getDamage() + "\n");

        return info;
    }
}
