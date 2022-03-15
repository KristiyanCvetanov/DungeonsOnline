package bg.sofia.uni.fmi.mjt.dungeon.treasure.potions;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;

import java.util.Objects;

public class ManaPotion extends AbstractPotion {

    public ManaPotion(String name, int manaPoints) {
        super(name, manaPoints);
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
        ManaPotion that = (ManaPotion) o;
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

        hero.takeMana(restorationPoints);
    }

    @Override
    public String getInfo() {
        String info = "";
        info += "    Treasure type: Mana potion\n";
        info += ("    Name: " + getName() + "\n");
        info += ("    Mana points: " + restorationPoints + "\n");
        return info;
    }
}
