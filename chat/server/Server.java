package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread {
    private final static int PORT = 33333;
    private List<User> clients;
    private ServerSocket server;

    public Server(int port) {
        try {
            this.server = new ServerSocket(port);
            System.out.println("Server ready!\n");
            clients = Collections.synchronizedList(new ArrayList<>());
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(PORT);
    }

    public void run() {
        try {
            // Wait for each client
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket client = server.accept();
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                // Fetch the login parameters
                String s = dataInputStream.readUTF();
                String operation = s.substring(0, 3);
                String username = s.substring(3);
                // Create new connected client
                User newClient = new User(client, username);
                // User login
                boolean login = true;
                if (operation.equalsIgnoreCase("LIN")) {
                    DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                    // Check the duplicate client
                    for (User cl : clients) {
                        if (cl.getUsername().equalsIgnoreCase(username)) {
                            // Notify the wrong connection to the client and close the connection
                            dataOutputStream.writeUTF("NOK");
                            login = false;
                            newClient.getClient().close();
                        }
                    }
                    // Add new User and notify the right connection to the client
                    if (login) {
                        clients.add(newClient);
                        dataOutputStream.writeUTF("OK");
                    }
                }
                // Listen to others messages received from client
                if (login)
                    new SendMessageToClients(clients, newClient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}