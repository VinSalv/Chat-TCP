package chat.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.net.Socket;

public class ReceiveMessageFromServer extends Thread {
    private final Socket client;
    private final JTextArea msg_Area;
    @SuppressWarnings("rawtypes")
    private final JList users_List;
    @SuppressWarnings("rawtypes")
    private final DefaultListModel listModel;
    private final JButton SEND_Button;

    @SuppressWarnings("rawtypes")
    public ReceiveMessageFromServer(Socket client, JTextArea msg_Area, JList users_List, DefaultListModel listModel,
                                    JButton SEND_Button) {
        this.client = client;
        this.msg_Area = msg_Area;
        this.users_List = users_List;
        this.listModel = listModel;
        this.SEND_Button = SEND_Button;
        this.start();
    }

    public void run() {
        try {
            if (client != null) {
                // Open input stream
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                String server_Input;
                // Read stream
                // noinspection ConstantConditions
                while ((server_Input = dataInputStream.readUTF()) != null) {
                    if (server_Input.length() > 0) {
                        @SuppressWarnings("DuplicatedCode")
                        String operation;
                        String msgText;
                        // Split server_input in operation (default string of 3 characters) and msgText (body of message)
                        // If the input is just 3 characters msgTex is set to empty string
                        if (server_Input.length() == 3) {
                            operation = server_Input.substring(0, 3);
                            msgText = "";
                        } else {
                            operation = server_Input.substring(0, 3);
                            msgText = server_Input.substring(3);
                        }
                        String[] users;
                        boolean disableSEND_Button = false;
                        boolean isSelected = false;
                        String userSelected = "";
                        // Switch case depending on operation string obtained from the server
                        switch (operation) {
                            //Update JList
                            case ("USR"):
                                // Find selected user
                                for (int i = 0; i < listModel.size(); i++) {
                                    if (users_List.isSelectedIndex(i)) {
                                        isSelected = true;
                                        userSelected = (String) listModel.getElementAt(i);
                                    }
                                }
                                // Reset JList
                                listModel.removeAllElements();
                                // Splitting msgText to obtain username
                                if (msgText.contains(">")) {
                                    users = msgText.split(">");
                                    for (String s : users) {
                                        //noinspection unchecked
                                        listModel.addElement(s);
                                    }
                                    //Update JList
                                    //noinspection unchecked
                                    users_List.setModel(listModel);
                                }
                                break;
                            // Logout user
                            case ("LUS"):
                                // Find and remove the user from the JList
                                int indexForRemoving = 0;
                                for (int i = 0; i < listModel.size(); i++) {
                                    if (listModel.getElementAt(i).equals(msgText)) indexForRemoving = i;
                                    if (users_List.isSelectedIndex(i)) {
                                        isSelected = true;
                                        userSelected = (String) listModel.getElementAt(i);
                                    }
                                }
                                // Disable SEND Button for clients who were communicating with disconnected client
                                if (listModel.getElementAt(indexForRemoving).equals(users_List.getSelectedValue())) {
                                    disableSEND_Button = true;
                                }
                                // Remove the user from JList
                                listModel.remove(indexForRemoving);
                                //noinspection unchecked
                                users_List.setModel(listModel);
                                break;
                            default:
                                // Print message on the JTextArea
                                msg_Area.append(msgText + "\n");
                        }
                        // Disable specific SEND_Button
                        if (disableSEND_Button)
                            SEND_Button.setEnabled(false);
                        // Hold the selection of the last selected user
                        if (isSelected)
                            for (int i = 0; i < listModel.size(); i++) {
                                if (listModel.getElementAt(i).equals(userSelected)) {
                                    users_List.setSelectedIndex(i);
                                    break;
                                }
                            }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}