package chatapp;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    Socket socket;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            ChatServer.clients.add(out);

            String msg;

            while ((msg = in.readLine()) != null) {

                System.out.println("Received: " + msg);

                for (PrintWriter writer : ChatServer.clients) {

                    writer.println(msg);
                }
            }

        } catch (Exception e) {

            System.out.println("Client disconnected");
        }
    }
}
