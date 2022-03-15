package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;

import java.util.Objects;

public class HealingPotion extends AbstractPotion {

    public HealingPotion(String name, int healingPoints) {
        super(name, healingPoints);
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
        HealingPotion that = (HealingPotion) o;
        return restorationPoints == that.restorationPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), restorationPoints);
    }

    @Override
    public void heal(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Given hero in heal method is null.\n");
        }

        hero.takeHealing(restorationPoints);
    }

    @Override
    public String getInfo() {
        String info;
        info = "    Treasure type: Healing potion\n";
        info += ("    Name: " + getName() + "\n");
        info += ("    Healing points: " + restorationPoints + "\n");
        return info;
    }
}
