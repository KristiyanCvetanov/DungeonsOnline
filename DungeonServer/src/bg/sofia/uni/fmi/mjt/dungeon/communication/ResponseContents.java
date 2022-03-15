package bg.sofia.uni.fmi.mjt.dungeon.communication;

import java.net.SocketAddress;
import java.util.Set;

public record ResponseContents(char[][] updatedMap,
                               String message,
                               Set<SocketAddress> clientsToSendMap,
                               Set<SocketAddress> clientsToSendMessage,
                               SocketAddress exitingPlayerAddress) {
}
