package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.RequestSenderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestSender {

    private final Socket socket;
    private final ResponseListener listener;

    public RequestSender(Socket socket, ResponseListener listener) {
        this.socket = socket;
        this.listener = listener;
    }

    public void start() {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // autoflush on
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));) {
            while (!listener.isStopped()) {
                if (!consoleReader.ready()) {
                    Thread.sleep(200);
                    continue;
                }

                String message = consoleReader.readLine(); // read a line from the console

                writer.println(message); // send the message to the server
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestSenderException("Problem with writing to server.\n", e);
        }
    }
}
