package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;
import bg.sofia.uni.fmi.mjt.dungeon.exception.InvalidItemException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.PlayerConflictException;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerExchangeCommand extends AbstractConflictCommand {

    private final int itemIndex;

    public PlayerExchangeCommand(SocketAddress addr,
                                 GameState game,
                                 int itemIndex) {
        super(addr, game);
        this.itemIndex = itemIndex;
    }

    @Override
    public ResponseContents execute() {
        String responseMessage;
        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();

        try {
            SocketAddress otherPlayerAddress = super.getOtherPlayerInConflict();  // throws exception

            responseMessage = getMessageAfterExchange(otherPlayerAddress);
            clientsToSendMessage.add(playerAddress);
            clientsToSendMessage.add(otherPlayerAddress);
        } catch (PlayerConflictException | InvalidItemException | IllegalArgumentException e) {
            responseMessage = e.getMessage();
            clientsToSendMessage.add(playerAddress);
        }

        return new ResponseContents(null,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }

    private String getMessageAfterExchange(SocketAddress receivingPlayerAddress) {
        Map<SocketAddress, Hero> heroes = game.getHeroes();

        Hero givingHero = heroes.get(playerAddress);
        Hero receivingHero = heroes.get(receivingPlayerAddress);

        String message;
        Treasure toGive = givingHero.getItem(itemIndex);

        if (receivingHero.addToBackpack(toGive)) {
            givingHero.dropItem(toGive);
            message = givingHero.getName() + " gave " + receivingHero.getName() + " their "
                    + toGive.getName() + "." + System.lineSeparator();
        } else {
            message = receivingHero.getName() + "'s backpack is full." + System.lineSeparator();
        }

        return message;
    }
}
