package bg.sofia.uni.fmi.mjt.dungeon.actor.minion;

import bg.sofia.uni.fmi.mjt.dungeon.actor.AbstractActor;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Stats;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;

public class Minion extends AbstractActor {

    public Minion(String name, int level, Stats stats, Weapon weapon, Armor armor, Spell spell) {
        super(name, level, stats, weapon, armor, spell);
    }

    public Minion(Minion other) {
        super(other.getName(),
                other.level,
                new Stats(other.stats.getHealth(),
                        0,
                        other.stats.getMana(),
                        0,
                        other.stats.getAttack(),
                        other.stats.getDefense()),
                other.weapon,
                other.armor,
                other.spell);
    }
}
