package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.ResponseSenderException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ResponseSender {

    private static final int BUFFER_SIZE = 2048;
    private final ResponseContents response;
    private final Selector selector;

    public ResponseSender(ResponseContents responseContents,
                          Selector selector) {
        this.response = responseContents;
        this.selector = selector;
    }

    public void send() { // could be over 20 lines :)
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            for (SelectionKey key : selector.keys()) {
                buffer.clear();

                if (key.interestOps() != SelectionKey.OP_READ) {
                    continue;
                }
                SocketChannel clientChannel = (SocketChannel) key.channel();
                // if the map should be sent to that player, put it in the buffer
                if (response.clientsToSendMap().contains(clientChannel.getRemoteAddress())) {
                    putMapInBuffer(buffer);
                }
                // if the message should be sent to that player, put it in the buffer
                if (response.clientsToSendMessage().contains(clientChannel.getRemoteAddress())) {
                    putMessageInBuffer(buffer);
                }

                sendResponse(clientChannel, buffer); // send the response

                if (clientChannel.getRemoteAddress().equals(response.exitingPlayerAddress())) {
                    clientChannel.close();
                }
            }
        } catch (IOException e) {
            throw new ResponseSenderException("Problem occurred while writing back to client.", e);
        }
    }

    private void putMapInBuffer(ByteBuffer buffer) {
        char[][] map = response.updatedMap();
        if (map != null) {
            buffer.put(mapToString(map).getBytes());
            buffer.put(System.lineSeparator().getBytes());
        }
    }

    private void putMessageInBuffer(ByteBuffer buffer) {
        buffer.put(response.message().getBytes());
    }

    private void sendResponse(SocketChannel channelToSend, ByteBuffer buffer) throws IOException {
        buffer.flip();

        channelToSend.write(buffer);
    }

    private String mapToString(char[][] map) {
        StringBuilder mapStr = new StringBuilder();

        for (char[] chars : map) {
            for (int j = 0; j < map[0].length; j++) {
                mapStr.append(chars[j])
                        .append(" ");
            }
            mapStr.append(System.lineSeparator());
        }

        return mapStr.toString();
    }
}

