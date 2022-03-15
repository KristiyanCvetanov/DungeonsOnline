package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.DungeonClientException;

import java.io.IOException;
import java.net.Socket;


public class DungeonClient {

    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("localhost", SERVER_PORT)) {

            ResponseListener listener = new ResponseListener(clientSocket);
            RequestSender sender = new RequestSender(clientSocket, listener);

            listener.start();
            sender.start();
        } catch (IOException e) {
            throw new DungeonClientException("Problem with setup-ing client.\n", e);
        }
    }
}
