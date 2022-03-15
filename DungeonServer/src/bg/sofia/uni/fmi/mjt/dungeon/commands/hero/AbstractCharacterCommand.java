package bg.sofia.uni.fmi.mjt.dungeon.commands.hero;

import bg.sofia.uni.fmi.mjt.dungeon.actor.hero.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCharacterCommand extends AbstractCommand {

    protected final Hero hero;

    public AbstractCharacterCommand(SocketAddress addr, Hero hero) {
        super(addr);
        this.hero = hero;
    }

    protected ResponseContents executeByMessage(String info) {
        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(null,
                info,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }
}
