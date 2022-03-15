package bg.sofia.uni.fmi.mjt.dungeon.actor.hero;

import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.util.TreeMap;
import java.util.Map;

public class Backpack {

    private static final int MAX_BAG_SIZE = 10;
    private final Map<Integer, Treasure> bag;

    public Backpack() {
        bag = new TreeMap<>();
    }

    public boolean isFull() {
        return bag.size() == MAX_BAG_SIZE;
    }

    public boolean add(Treasure treasure) {
        if (isFull()) {
            return false;
        }

        for (int i = 1; i <= MAX_BAG_SIZE; i++) {
            if (!bag.containsKey(i)) {
                bag.put(i, treasure);
                return true;
            }
        }

        return false;
    }

    public Treasure get(int index) {
        if (!bag.containsKey(index)) {
            throw new IllegalArgumentException("There is no such item in your backpack."
                    + System.lineSeparator());
        }

        return bag.get(index);
    }

    public void remove(Treasure treasure) {
        for (var entry : bag.entrySet()) {
            if (treasure.equals(entry.getValue())) {
                bag.remove(entry.getKey());
                return;
            }
        }

        throw new InvalidItemException("There is no such item to remove.");
    }

    public int getSize() {
        return bag.size();
    }

    public String getContentInfo() {
        if (bag.isEmpty()) {
            return "Backpack is empty." + System.lineSeparator();
        }

        StringBuilder info = new StringBuilder("Backpack contents:\n");

        for (var entry : bag.entrySet()) {
            info.append("  ")
                    .append(entry.getKey())
                    .append(". ")
                    .append(entry.getValue().getName())
                    .append("\n");
        }

        return info.toString();
    }
}
