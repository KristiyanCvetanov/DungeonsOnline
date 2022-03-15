package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.Command;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public abstract class ItemInteractionCommand implements Command {

    protected final SocketAddress playerAddress;
    protected final Hero hero;
    protected final int itemIndex;

    public ItemInteractionCommand(SocketAddress addr, Hero hero, int itemIndex) {
        this.playerAddress = addr;
        this.hero = hero;
        this.itemIndex = itemIndex;
    }

    protected Set<SocketAddress> getClientsToSendMessage() {
        Set<SocketAddress> clientAddresses = new HashSet<>();
        clientAddresses.add(playerAddress);

        return clientAddresses;
    }
}
