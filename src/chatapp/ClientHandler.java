package chatapp;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            ChatServer.clients.add(out);

            String msg;

            while ((msg = in.readLine()) != null) {

                System.out.println("Received: " + msg);

                synchronized (ChatServer.clients) {
                    for (PrintWriter writer : ChatServer.clients) {
                        writer.println(msg);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Client disconnected");

        } finally {
            ChatServer.clients.remove(out);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
