package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ItemThrowCommand extends ItemInteractionCommand {

    public ItemThrowCommand(SocketAddress addr, Hero hero, int itemIndex) {
        super(addr, hero, itemIndex);
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        try {
            Treasure toDrop = hero.getItem(itemIndex);   // throws InvalidItemException
            hero.dropItem(toDrop);   // throws IllegalArgumentException
            responseMessage = toDrop.getName() + " thrown away." + System.lineSeparator();
        } catch (InvalidItemException | IllegalArgumentException e) {
            responseMessage = e.getMessage();
        }

        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = super.getClientsToSendMessage();

        return new ResponseContents(null,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }
}
