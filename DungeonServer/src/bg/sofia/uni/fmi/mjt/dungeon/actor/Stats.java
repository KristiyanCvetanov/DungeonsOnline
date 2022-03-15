package bg.sofia.uni.fmi.mjt.dungeon.actor;

import bg.sofia.uni.fmi.mjt.dungeon.treasure.gear.Armor;

public class Stats {

    private int currentHealth;
    private int maxNaturalHealth;
    private int bonusHealth;

    private int currentMana;
    private int maxNaturalMana;
    private int bonusMana;

    private int attack;
    private int defense;

    public Stats() {
        maxNaturalHealth = 100;
        bonusHealth = 0;
        currentHealth = maxNaturalHealth + bonusHealth;

        maxNaturalMana = 100;
        bonusMana = 0;
        currentMana = maxNaturalMana + bonusMana;

        attack = 50;
        defense = 50;
    }

    public Stats(int health, int bonusHealth, int mana, int bonusMana, int attack, int defense) {
        this.maxNaturalHealth = health;
        this.bonusHealth = bonusHealth;
        currentHealth = this.maxNaturalHealth + this.bonusHealth;

        this.maxNaturalMana = mana;
        this.bonusMana = bonusMana;
        currentMana = this.maxNaturalMana + this.bonusMana;

        this.attack = attack;
        this.defense = defense;
    }

    public int getHealth() {
        return currentHealth;
    }

    public int getMana() {
        return currentMana;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public void levelUpStats() {
        maxNaturalHealth += 10;
        currentHealth = maxNaturalHealth + bonusHealth;
        maxNaturalMana += 10;
        currentMana = maxNaturalMana + bonusMana;
        attack += 5;
        defense += 5;
    }

    public void addToHealth(int healingPoints) {
        if (healingPoints < 0) {
            throw new IllegalArgumentException("Healing points given in addToHealth method are negative.\n");
        }

        currentHealth = Math.min(currentHealth + healingPoints, maxNaturalHealth + bonusHealth);
    }

    public void addToMana(int manaPoints) {
        if (manaPoints < 0) {
            throw new IllegalArgumentException("Mana points given in addToMana method are negative.\n");
        }

        currentMana = Math.min(currentMana + manaPoints, maxNaturalMana + bonusMana);
    }

    public void extractFromHealth(int damageTaken) {
        if (damageTaken < 0) {
            throw new IllegalArgumentException("Damage points given in extractFromHealth method are negative.\n");
        }

        currentHealth = Math.max(currentHealth - damageTaken, 0);
    }

    public void extractFromMana(int manaConsumed) {
        if (manaConsumed < 0) {
            throw new IllegalArgumentException("Mana points given in extractFromMana method are negative.\n");
        }

        currentMana = Math.max(currentMana - manaConsumed, 0);
    }

    public void updateBonusHealth(Armor newArmor) {
        if (newArmor == null) {
            throw new IllegalArgumentException("Given armor in updateBonusHealth method is null.\n");
        }

        final int oldBonusHealth = bonusHealth;
        bonusHealth = newArmor.getHealth();

        int potentialCurrentHealth = currentHealth - oldBonusHealth + bonusHealth;
        if (potentialCurrentHealth > 0) {
            currentHealth = potentialCurrentHealth;
        }
    }
}
