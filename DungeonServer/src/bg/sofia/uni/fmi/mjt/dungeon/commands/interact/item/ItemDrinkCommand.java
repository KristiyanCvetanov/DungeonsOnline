package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ItemDrinkCommand extends ItemInteractionCommand {

    public ItemDrinkCommand(SocketAddress addr, Hero hero, int itemIndex) {
        super(addr, hero, itemIndex);
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        try {
            Treasure toDrink = hero.getItem(itemIndex);
            if (toDrink.getType() != TreasureType.DRINKABLE) {
                responseMessage = "Only potions can be drank." + System.lineSeparator();
            } else {
                toDrink.use(hero);
                responseMessage = toDrink.getName() + " drank." + System.lineSeparator();
            }
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
