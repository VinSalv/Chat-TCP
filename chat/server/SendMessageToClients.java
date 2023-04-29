package chat.server;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class SendMessageToClients extends Thread {
    private final List<User> clients;
    private final User client;

    public SendMessageToClients(List<User> clients, User client) {
        this.clients = clients;
        this.client = client;
        this.start();
    }

    public void run() {
        try {
            if (clients.size() > 0) {
                // Open input stream
                DataInputStream dataInputStream = new DataInputStream(client.getClient().getInputStream());
                String user_Input;
                // Read stream
                // noinspection ConstantConditions
                while ((user_Input = dataInputStream.readUTF()) != null) {
                    if (user_Input.length() > 0) {
                        @SuppressWarnings("DuplicatedCode")
                        String operation;
                        String msgText;
                        // Split user_input in operation (default string of 3 characters) and msgText (body of message)
                        // If the input is just 3 characters msgTex is set to empty string
                        if (user_Input.length() == 3) {
                            operation = user_Input.substring(0, 3);
                            msgText = "";
                        } else {
                            operation = user_Input.substring(0, 3);
                            msgText = user_Input.substring(3);
                        }
                        ArrayList<String> usernameList = new ArrayList<>();
                        // Switch case depending on operation string obtained from the client
                        switch (operation) {
                            // User logout
                            case ("LOT"):
                                // Find client to remove
                                int i = 0;
                                for (User u : clients) {
                                    if (u.getClient() == client.getClient()) {
                                        break;
                                    }
                                    i++;
                                }
                                // Remove the client
                                clients.remove(i);
                                client.getOut().writeUTF("DEF" + "Disconnection of client: " + client.getUsername());
                                // Communicate the disconnected user to others clients
                                for (User us : clients) {
                                    us.getOut().writeUTF("LUS" + client.getUsername());
                                }
                                client.getClient().close();
                                break;
                            // Broadcast
                            case ("BRD"):
                                // Send the message to all the connected users
                                for (User u : clients) {
                                    if (u != client) {
                                        u.getOut().writeUTF("DFT" + client.getUsername() + " >>  " + msgText);
                                    } else {
                                        u.getOut().writeUTF("DFT" + "Me >>  " + msgText);
                                    }
                                    u.getOut().flush();
                                }
                                break;
                            // OneToOne
                            case ("OTO"):
                                // Get the recipient user
                                String[] usrMsg;
                                if (msgText.contains("<")) {
                                    usrMsg = msgText.split("<");
                                    // Send the message to the recipient user
                                    for (User u : clients) {
                                        if (u.getUsername().equals(usrMsg[0])) {
                                            u.getOut().writeUTF("DFT" + client.getUsername() + " (private) >>  " + usrMsg[1]);
                                            u.getOut().flush();
                                        } else if (client.getUsername().equals(u.getUsername())) {
                                            client.getOut().writeUTF("DFT" + "Me >>  " + usrMsg[1]);
                                            client.getOut().flush();
                                        }
                                    }
                                }
                                break;
                            // List
                            case ("LST"):
                                // Send a new string containing all usernames to others clients
                                for (User a : clients)
                                    usernameList.add(a.getUsername());
                                for (User us : clients) {
                                    StringBuilder temp = new StringBuilder();
                                    for (String str : usernameList) {
                                        if (!str.equals(us.getUsername()))
                                            temp.append(str).append(">");
                                    }
                                    us.getOut().writeUTF("USR" + temp);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}