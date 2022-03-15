package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ItemInfoCommand extends ItemInteractionCommand {

    public ItemInfoCommand(SocketAddress addr, Hero hero, int itemIndex) {
        super(addr, hero, itemIndex);
    }

    @Override
    public ResponseContents execute() {
        String info;
        try {
            info = hero.getItem(itemIndex).getInfo();
        } catch (IllegalArgumentException e) {
            info = e.getMessage();
        }

        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = super.getClientsToSendMessage();

        return new ResponseContents(null,
                info,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }
}
