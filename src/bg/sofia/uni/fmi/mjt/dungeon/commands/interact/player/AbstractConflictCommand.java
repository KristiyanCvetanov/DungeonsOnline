package bg.sofia.uni.fmi.mjt.dungeon.commands.interact.player;

import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractGameCommand;
import bg.sofia.uni.fmi.mjt.dungeon.exception.PlayerConflictException;
import bg.sofia.uni.fmi.mjt.dungeon.game.state.GameState;

import java.net.SocketAddress;
import java.util.Map;

public abstract class AbstractConflictCommand extends AbstractGameCommand {

    public AbstractConflictCommand(SocketAddress addr, GameState game) {
        super(addr, game);
    }

    protected SocketAddress getOtherPlayerInConflict() {
        Map<SocketAddress, Integer> players = game.getPlayersAddresses();
        Map<Integer, Integer> playersInConflict = game.getPlayersInConflict();
        int playerIcon = players.get(playerAddress);

        if (playersInConflict.containsKey(playerIcon)) {
            int otherPlayerIcon = playersInConflict.get(playerIcon);

            for (var entry : players.entrySet()) {
                if (otherPlayerIcon == entry.getValue()) {
                    return entry.getKey();
                }
            }
        }

        throw new PlayerConflictException("You are not in a position to exchange with anybody."
                + System.lineSeparator());
    }
}
