package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.item;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.TreasureType;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ItemEquipCommand extends ItemInteractionCommand {

    public ItemEquipCommand(SocketAddress addr, Hero hero, int itemIndex) {
        super(addr, hero, itemIndex);
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        try {
            Treasure toEquip = hero.getItem(itemIndex);
            if (toEquip.getType() != TreasureType.EQUIPPABLE) {
                responseMessage = "You can equip only weapons and armor." + System.lineSeparator();
            } else {
                responseMessage = getMessageAfterEquip(toEquip);
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

    private String getMessageAfterEquip(Treasure toEquip) {
        if (toEquip.use(hero)) {
            return toEquip.getName() + " equipped." + System.lineSeparator();
        } else {
            return "You don't have the required level to equip that item." + System.lineSeparator();
        }
    }
}
