package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.ServerStoppingException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.ServerThreadException;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.Iterator;

public class ServerThread extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_PLAYERS_COUNT = 10;
    private static final String TREASURE_DIRECTORY = "treasures.txt";
    private static final String MINION_DIRECTORY = "minions.txt";
    private final ServerSocketChannel serverSocketChannel;
    private final String mapDirectory;
    private ServerResponse serverResponse;
    private Selector selector;

    public ServerThread(ServerSocketChannel serverSocketChannel,
                        String mapDirectory) {
        this.serverSocketChannel = serverSocketChannel;
        this.mapDirectory = mapDirectory;
    }

    @Override
    public void run() {
        try (Reader mapReader = Files.newBufferedReader(Path.of(mapDirectory));
             Reader treasureReader = Files.newBufferedReader(Path.of(TREASURE_DIRECTORY));
             Reader minionReader = Files.newBufferedReader(Path.of(MINION_DIRECTORY))) {
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            serverResponse = new ServerResponse(mapReader, treasureReader, minionReader);

            System.out.println("Server started.");
            while (serverSocketChannel.isOpen()) {
                final int selectTimeOut = 1_000;
                int readyChannels = selector.select(selectTimeOut);
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                iterateSelectedKeys(selectedKeys, buffer);
            }
            selector.close();
        } catch (IOException e) {
            throw new ServerThreadException("An I/O problem occurred while running server thread.", e);
        }
    }

    public void stopThread() {
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            throw new ServerStoppingException("There is a problem with closing the server socket.", e);
        }
    }

    private void iterateSelectedKeys(final Set<SelectionKey> selectedKeys,
                                     final ByteBuffer buffer) throws IOException {
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isReadable()) {
                SocketChannel currClientChannel = (SocketChannel) key.channel();
                serveClient(currClientChannel, buffer);
            } else if (key.isAcceptable()) {
                ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                registerClient(serverChannel, buffer);
            }

            keyIterator.remove();
        }
    }

    private void sendAnswer(final SocketChannel currClientChannel, final ByteBuffer buffer, final String answer) {
        buffer.clear();
        buffer.put(answer.getBytes());
        buffer.flip();

        try {
            currClientChannel.write(buffer);
        } catch (IOException e) {
            System.out.println("There is a problem when writing back to the client.");
            e.printStackTrace();
        }
    }

    private void serveClient(SocketChannel currClientChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        if (currClientChannel.read(buffer) <= 0) {
            System.out.println("Client connection closed.");
            currClientChannel.close();
            return;
        }

        buffer.flip();
        String request = StandardCharsets.UTF_8.decode(buffer).toString();

        // clients cannot use this command
        if (request.startsWith("register")) {
            String response = "Unknown Command." + System.lineSeparator();
            sendAnswer(currClientChannel, buffer, response);
        }

        ResponseContents response = serverResponse.formulateAnswer(request, currClientChannel.getRemoteAddress());
        ResponseSender sender = new ResponseSender(response, selector);
        sender.send();
    }

    private void registerClient(ServerSocketChannel serverChannel, ByteBuffer buffer) throws IOException {
        SocketChannel accepted = serverChannel.accept();
        accepted.configureBlocking(false);

        // check if game is full
        if (selector.keys().size() == MAX_PLAYERS_COUNT) {
            String answer = "Server is full!" + System.lineSeparator();
            sendAnswer(accepted, buffer, answer);
            accepted.close();
        } else {
            accepted.configureBlocking(false);
            accepted.register(selector, SelectionKey.OP_READ);
            registerPlayer(accepted);
        }
    }

    private void registerPlayer(SocketChannel accepted) throws IOException {
        String request = "register" + System.lineSeparator();
        ResponseContents response = serverResponse.formulateAnswer(request, accepted.getRemoteAddress());

        ResponseSender sender = new ResponseSender(response, selector);
        sender.send();
    }
}
