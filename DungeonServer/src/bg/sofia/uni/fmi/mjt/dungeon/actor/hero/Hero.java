package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import bg.sofia.uni.fmi.mjt.dungeon.actor.AbstractActor;
import bg.sofia.uni.fmi.mjt.dungeon.actor.Stats;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Spell;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Weapon;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.potions.Drinkable;

public class Hero extends AbstractActor implements HeroActions {

    private static final ExperienceBar XP_BAR = ExperienceBar.getInstance();
    private final Backpack backpack;
    private int experience;

    public Hero(String name) {
        super(name,
                1,
                new Stats(),
                new Weapon(),
                new Armor(),
                null);

        experience = 0;
        backpack = new Backpack();
    }

    @Override
    public void takeHealing(int healingPoints) {
        if (healingPoints < 0) {
            throw new IllegalArgumentException("Given healing points in takeHealing method are negative.\n");
        }

        stats.addToHealth(healingPoints);
    }

    @Override
    public void takeMana(int manaPoints) {
        if (manaPoints < 0) {
            throw new IllegalArgumentException("Given mana points in takeMana method are negative.\n");
        }

        stats.addToMana(manaPoints);
    }

    @Override
    public boolean equip(Weapon newWeapon) {
        if (newWeapon == null) {
            throw new IllegalArgumentException("Given weapon in equip method is null.\n");
        }

        if (level < newWeapon.getLevel()) {
            return false;
        }

        backpack.remove(newWeapon);
        backpack.add(weapon);
        weapon = newWeapon;

        return true;
    }

    @Override
    public boolean equip(Armor newArmor) {
        if (newArmor == null) {
            throw new IllegalArgumentException("Given armor in equip method is null.\n");
        }

        if (level < newArmor.getLevel()) {
            return false;
        }

        backpack.remove(newArmor);
        backpack.add(armor);
        armor = newArmor;
        stats.updateBonusHealth(newArmor);

        return true;
    }

    @Override
    public boolean learn(Spell newSpell) {
        if (newSpell == null) {
            throw new IllegalArgumentException("Given spell in learn method is null.\n");
        }

        if (level < newSpell.getLevel()) {
            return false;
        }

        backpack.remove(newSpell);
        spell = newSpell;

        return true;
    }

    @Override
    public boolean drink(Drinkable potion) {
        if (potion == null) {
            throw new IllegalArgumentException("Given potion in drink method is null.\n");
        }

        backpack.remove(potion);
        potion.heal(this);

        return true;
    }

    @Override
    public boolean addToBackpack(Treasure treasure) {
        if (treasure == null) {
            throw new IllegalArgumentException("Given treasure in addToBackpack method is null.\n");
        }

        return backpack.add(treasure);
    }

    @Override
    public int gainExperience(int xpPoints) {
        if (xpPoints < 0) {
            throw new IllegalArgumentException("Given experience points in gainExperience method are negative.\n");
        }

        experience = Math.min(experience + xpPoints, ExperienceBar.getExperienceCap());

        int newLevel = XP_BAR.levelUp(experience);
        int levelsGained = newLevel - level;
        for (int i = 0; i < levelsGained; i++) {
            stats.levelUpStats();
        }

        level = newLevel;
        return levelsGained;
    }

    @Override
    public Treasure getItem(int index) {
        return backpack.get(index);
    }

    @Override
    public void dropItem(Treasure treasure) {
        if (treasure == null) {
            throw new IllegalArgumentException("Given treasure in dropItem method is null.\n");
        }

        backpack.remove(treasure);
    }

    @Override
    public String getCharacterInfo() {
        String info;
        info = "Hero name: " + super.getName() + "\n";
        info += ("Level: " + super.getLevel() + "\n");
        info += ("Experience: " + experience
                + " (need for next level: " + XP_BAR.neededXpForNextLevel(experience) + ")\n");
        info += ("Stats:\n" + super.getStatsInfo());
        info += ("Weapon:\n" + super.getWeapon().getInfo());
        info += ("Armor:\n" + super.getArmor().getInfo());

        String spellInfo = getSpell() == null ? "    None\n" : getSpell().getInfo();
        info += ("Spell:\n" + spellInfo);

        return info;
    }

    @Override
    public String getBackpackInfo() {
        return backpack.getContentInfo();
    }

    @Override
    public int getExperience() {
        return experience;
    }
}
