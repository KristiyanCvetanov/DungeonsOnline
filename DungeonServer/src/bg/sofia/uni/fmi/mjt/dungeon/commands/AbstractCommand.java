package bg.sofia.uni.fmi.mjt.dungeon.commands;

import java.net.SocketAddress;

public abstract class AbstractCommand implements Command {

    protected final SocketAddress playerAddress;

    public AbstractCommand(SocketAddress addr) {
        this.playerAddress = addr;
    }
}
