package bg.sofia.uni.fmi.mjt.dungeon.communication;

import bg.sofia.uni.fmi.mjt.dungeon.exception.ResponseListenerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ResponseListener extends Thread {

    private final Socket socket;
    private boolean stopped;

    public ResponseListener(Socket socket) {
        this.socket = socket;
        this.stopped = false;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String reply;
            while (true) {
                reply = reader.readLine(); // read the response from the server

                if (reply == null) {
                    stopped = true;
                    break;
                }

                System.out.println(reply);
            }
        } catch (IOException e) {
            throw new ResponseListenerException("Problem with reading response from server.\n", e);
        }
    }

    public boolean isStopped() {
        return stopped;
    }
}
