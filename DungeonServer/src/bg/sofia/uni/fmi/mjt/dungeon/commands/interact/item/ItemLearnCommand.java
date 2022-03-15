package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ItemLearnCommand extends ItemInteractionCommand {

    public ItemLearnCommand(SocketAddress addr, Hero hero, int itemIndex) {
        super(addr, hero, itemIndex);
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        try {
            Treasure toLearn = hero.getItem(itemIndex);
            if (toLearn.getType() != TreasureType.LEARNABLE) {
                responseMessage = "You can learn only spells." + System.lineSeparator();
            } else {
                responseMessage = getMessageAfterLearn(toLearn);
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

    private String getMessageAfterLearn(Treasure toLearn) {
        if (toLearn.use(hero)) {
            return toLearn.getName() + " learned." + System.lineSeparator();
        } else {
            return "You don't have the required level to learn that spell." + System.lineSeparator();
        }
    }
}
