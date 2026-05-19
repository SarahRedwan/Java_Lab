package chatapp;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    // Store all connected clients
    static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(1234);

        System.out.println("Server started...");

        while (true) {

            Socket socket = server.accept();

            System.out.println("Client connected");

            ClientHandler clientThread = new ClientHandler(socket);

            clientThread.start();
        }
    }
}
