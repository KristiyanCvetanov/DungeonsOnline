package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.ServerConfigurationException;
import bg.sofia.uni.fmi.mjt.dungeon.exception.ServerStoppingException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class DungeonServer {

    private static final String ACHIEVEMENT_FILE_NAME = "achievements.txt";
    private static final String SERVER_HOST = "localhost";
    private final int serverPort;
    private final String mapDirectory;
    private ServerSocketChannel serverSocketChannel;
    private ServerThread serverThread;

    public DungeonServer(int port) {
        serverPort = port;
        mapDirectory = "map10x10.txt";
    }

    public DungeonServer(int port, String mapDirectory) {
        serverPort = port;
        this.mapDirectory = mapDirectory;
    }

    public void start() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocketChannel.configureBlocking(false);

            serverThread = new ServerThread(serverSocketChannel, mapDirectory);
            serverThread.start();

            createAchievementFile();
        } catch (IOException e) {
            throw new ServerConfigurationException("An I/O problem occurred while configuring server.", e);
        }
    }

    public void stop() {
        try {
            serverSocketChannel.close();
            System.out.println("Server closed.");

            serverThread.stopThread();
            Files.delete(Path.of(ACHIEVEMENT_FILE_NAME));
        } catch (IOException e) {
            throw new ServerStoppingException("There is a problem with closing the server socket", e);
        }
    }

    private void createAchievementFile() throws IOException {
        try (var writer = new PrintWriter(new FileWriter(ACHIEVEMENT_FILE_NAME), true)) {
            final String lineSuffix = ", treasures found: 0, minions killed: 0, players killed: 0"
                    + System.lineSeparator();

            final int maxNumOfPlayers = 9;
            for (int i = 1; i <= maxNumOfPlayers; i++) {
                String line = "Player " + i + lineSuffix;
                writer.write(line);
            }
        }
    }
}
