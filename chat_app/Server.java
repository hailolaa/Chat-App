package com.example.chat_app;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<ClientHandler> clients_list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
       try {
           ServerSocket serverSocket = new ServerSocket(4000);
           System.out.println("Server started ");
           System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clients_list.add(clientHandler);
            new Thread(clientHandler).start();
        }
       }
       catch (IOException e){
           System.out.println("Error creating server");
           e.printStackTrace();
       }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine();
                System.out.println(username + " connected.");
                broadcastMessage(username + " has joined the chat");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(username + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(username + " disconnected.");
                broadcastMessage(username + " has left the chat");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients_list.remove(this);

            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients_list) {
                client.out.println(message);
            }
        }
    }
}
