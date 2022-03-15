package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;

public abstract class AbstractActor implements Actor {

    private final String name;
    protected final Stats stats;
    protected int level;
    protected Weapon weapon;
    protected Armor armor;
    protected Spell spell;

    public AbstractActor(String name, int level, Stats stats, Weapon weapon, Armor armor, Spell spell) {
        this.name = name;
        this.stats = stats;
        this.weapon = weapon;
        this.armor = armor;
        this.spell = spell;
        this.level = level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAlive() {
        return stats.getHealth() > 0;
    }

    @Override
    public int takeDamage(int damagePoints) {
        if (damagePoints < 0) {
            throw new IllegalArgumentException("Given damage points in takeDamage method are negative.\n");
        }

        int damage = calculateDamageTaken(damagePoints);

        stats.extractFromHealth(damage);

        return damage;
    }

    @Override
    public int attack() {
        int bonusAttack;
        boolean spellIsStronger = spell != null && weapon.getDamage() < spell.getDamage();

        if (spellIsStronger && stats.getMana() >= spell.getManaCost()) {
            bonusAttack = spell.getDamage();
            stats.extractFromMana(spell.getManaCost());
        } else {
            bonusAttack = weapon.getDamage();
        }

        return stats.getAttack() + bonusAttack;
    }

    @Override
    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public Armor getArmor() {
        return armor;
    }

    @Override
    public Spell getSpell() {
        return spell;
    }

    public int getLevel() {
        return level;
    }

    public Stats getStats() {
        return stats;
    }

    public String getStatsInfo() {
        String info;

        info = "    health: " + stats.getHealth() + "\n";
        info += ("    mana: " + stats.getMana() + "\n");

        int bonusAttack = spell == null ? weapon.getDamage() : Math.max(weapon.getDamage(), spell.getDamage());
        info += ("    attack: " + (stats.getAttack() + bonusAttack) + "\n");
        info += ("    defence: " + (stats.getDefense() + armor.getDefence()) + "\n");

        return info;
    }

    // based on stats, armor and weapon/spell(whichever stronger)
    public int getExperienceWorth() {
        final int healthPowerIndex = 5;
        final int manaPowerIndex = 3;
        final int attackPowerIndex = 10;
        final int defencePowerIndex = 5;

        final int statsPowerIndex = stats.getHealth() * healthPowerIndex
                + stats.getMana() * manaPowerIndex
                + stats.getAttack() * attackPowerIndex
                + stats.getDefense() * defencePowerIndex;

        final int weaponPowerIndex = weapon.getPower();
        final int spellPowerIndex = spell == null ? 0 : spell.getPower();
        final int armorPowerIndex = armor.getPower();

        return statsPowerIndex
                + weaponPowerIndex
                + spellPowerIndex
                + armorPowerIndex;
    }

    private int calculateDamageTaken(int damagePoints) {
        final int balanceIndex = 5;
        final double blockPercentage = (double) (stats.getDefense() + armor.getDefence()) / balanceIndex;

        final int hundred = 100;
        double actualDamage = (double) damagePoints / hundred * (hundred - blockPercentage);
        return (int) Math.round(actualDamage);
    }
}
