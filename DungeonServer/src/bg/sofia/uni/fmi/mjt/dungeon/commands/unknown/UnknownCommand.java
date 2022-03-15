package bg.sofia.uni.fmi.mjt.dungeon.commands.unknown;

import bg.sofia.uni.fmi.mjt.dungeon.commands.AbstractCommand;
import bg.sofia.uni.fmi.mjt.dungeon.communication.ResponseContents;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class UnknownCommand extends AbstractCommand {

    private final String responseMessage;

    public UnknownCommand(SocketAddress addr, String message) {
        super(addr);
        this.responseMessage = message;
    }

    @Override
    public ResponseContents execute() {
        Set<SocketAddress> clientsToSendMap = new HashSet<>();
        Set<SocketAddress> clientsToSendMessage = new HashSet<>();
        clientsToSendMessage.add(playerAddress);

        return new ResponseContents(null,
                responseMessage,
                clientsToSendMap,
                clientsToSendMessage,
                null);
    }
}
