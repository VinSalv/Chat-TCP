package chat.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
    private final String username;
    private final Socket client;
    private DataOutputStream out;

    public User(Socket client, String username) {
        this.client = client;
        this.username = username;
        try {
            this.out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public Socket getClient() {
        return client;
    }

    public DataOutputStream getOut() {
        return out;
    }
}