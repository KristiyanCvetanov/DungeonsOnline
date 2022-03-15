package bg.sofia.uni.fmi.mjt.dungeon.battle.engine;

import bg.sofia.uni.fmi.mjt.dungeon.actor.Actor;

public class BattleEngine {

    private final Actor firstAttacker;
    private final Actor secondAttacker;
    private StringBuilder commentary;
    private boolean performed;

    public BattleEngine(Actor actor1, Actor actor2) {
        if (actor1 == actor2) {
            throw new IllegalArgumentException("The two actors in battle are the same.\n");
        }

        this.firstAttacker = actor1;
        this.secondAttacker = actor2;
        commentary = new StringBuilder("The battle has not been performed yet.\n");
        performed = false;
    }

    public void perform() {
        if (performed) {
            throw new UnsupportedOperationException("The battle has already been performed.\n");
        }
        commentary = new StringBuilder();
        commentary.append("A battle between ").
                append(firstAttacker.getName()).append(" and ").
                append(secondAttacker.getName()).append(" has begun!\n");

        while (true) {
            oneAttacksOther(firstAttacker, secondAttacker);
            if (!secondAttacker.isAlive()) {
                break;
            }

            oneAttacksOther(secondAttacker, firstAttacker);
            if (!firstAttacker.isAlive()) {
                break;
            }
        }

        performed = true;
    }

    public String getCommentary() {
        return commentary.toString();
    }

    private void oneAttacksOther(Actor attacker, Actor defender) {
        int damageTakenByDefender = defender.takeDamage(attacker.attack());
        commentary.append(attacker.getName())
                .append(" dealt ")
                .append(damageTakenByDefender)
                .append(" damage to ")
                .append(defender.getName()).append("...\n");

        if (!defender.isAlive()) {
            commentary.append("Battle has ended!\n");
            commentary.append("Winner is ").append(attacker.getName()).append(".\n");
            commentary.append("Defeated is ").append(defender.getName()).append(".\n");
        }
    }
}
