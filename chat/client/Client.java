package chat.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends JFrame {
    private final static int PORT = 33333;
    private static DataOutputStream dataOutputStream;
    @SuppressWarnings("rawtypes")
    private final DefaultListModel listModel;
    private String name;
    private String usrDST;
    private JPanel MAIN_Panel;
    private JButton LOGIN_Button;
    private JButton LOGOUT_Button;
    private JButton BRD_Button;
    private JButton SEND_Button;
    @SuppressWarnings("rawtypes")
    private JList USERS_List;
    private JTextArea MSG_Area;
    private JTextField MSG_Text;
    private JLabel TEXT_Label;
    private JPanel LIST_Panel;
    private JPanel TEXT_AREA_Panel;

    @SuppressWarnings("rawtypes")
    public Client() {
        listModel = new DefaultListModel();
        usrDST = "";
        initComponents();
        initButtonListeners();
    }

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        // Create frame
        Client frame = new Client();
        frame.setVisible(true);
        try {
            String result = "";
            Socket socket = null;
            dataOutputStream = null;
            DataInputStream dataInputStream;
            // Get necessary GUI parameters
            JTextArea msg_Area = frame.getMSG_Area();
            JList users_List = frame.getUSERS_List();
            DefaultListModel listModel = frame.getListModel();
            JButton SEND_Button = frame.getSEND_Button();
            JButton LOGIN_Button = frame.getLOGIN_Button();
            JButton LOGOUT_Button = frame.getLOGOUT_Button();
            JButton BRD_Button = frame.getBRD_Button();
            JLabel TEXT_Label = frame.getTEXT_Label();
            JTextField MSG_Text = frame.getMSG_Text();
            // While repeats itself until the chosen username is already connected
            while (!result.equals("OK")) {
                // Socket and streams initialization
                socket = new Socket("localhost", PORT);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                result = dataInputStream.readUTF();
                if (result.equals("NOK")) {
                    // Alert message to indicate the username is already used
                    JOptionPane.showMessageDialog(null, "Username already used, use another username");
                }
                // Setting GUI components
                LOGIN_Button.setEnabled(true);
                LOGOUT_Button.setEnabled(false);
                BRD_Button.setEnabled(false);
                SEND_Button.setEnabled(false);
                frame.setTitle("Login to chat");
                TEXT_Label.setText("Username:");
                MSG_Text.setText("");
            }
            String name = frame.getName();
            // Setting GUI components
            LOGIN_Button.setEnabled(false);
            LOGOUT_Button.setEnabled(true);
            BRD_Button.setEnabled(true);
            frame.setTitle(name + " (Connected)");
            TEXT_Label.setText("Message to send:");
            MSG_Text.setText("");
            // Add users to the JList
            dataOutputStream.writeUTF("LST");
            // Listen to others messages received from server
            new ReceiveMessageFromServer(socket, msg_Area, users_List, listModel, SEND_Button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initialized frame components
    private void initComponents() {
        this.setBounds(100, 100, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(MAIN_Panel);
        this.setTitle("Login to chat");
        LOGOUT_Button.setEnabled(false);
        BRD_Button.setEnabled(false);
        SEND_Button.setEnabled(false);
    }

    // Perform action listener
    private void initButtonListeners() {
        LOGIN_Button.addActionListener(e -> {
            try {
                if (!MSG_Text.getText().equals("")) {
                    String msgToSend = "LIN" + MSG_Text.getText().trim();
                    name = msgToSend.substring(3);
                    dataOutputStream.writeUTF(msgToSend);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        LOGOUT_Button.addActionListener(e -> {
            try {
                String msgToSend = "LOT".trim();
                dataOutputStream.writeUTF(msgToSend);
                LOGIN_Button.setEnabled(false);
                LOGOUT_Button.setEnabled(false);
                BRD_Button.setEnabled(false);
                SEND_Button.setEnabled(false);
                USERS_List.setEnabled(false);
                MSG_Area.setEnabled(false);
                MSG_Text.setEnabled(false);
                this.setTitle(name + " (Disconnected)");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        BRD_Button.addActionListener(e -> {
            try {
                if (!MSG_Text.getText().equals("")) {
                    String msgToSend = "BRD" + MSG_Text.getText().trim();
                    dataOutputStream.writeUTF(msgToSend);
                    MSG_Text.setText("");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        USERS_List.addListSelectionListener(e -> {
            try {
                usrDST = (String) USERS_List.getSelectedValue();
                SEND_Button.setEnabled(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        SEND_Button.addActionListener(e -> {
            try {
                if (!MSG_Text.getText().equals("") && !usrDST.equals("")) {
                    String msgToSend = "OTO" + usrDST + "<" + MSG_Text.getText().trim();
                    dataOutputStream.writeUTF(msgToSend);
                    MSG_Text.setText("");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    // Get methods
    public JTextArea getMSG_Area() {
        return MSG_Area;
    }

    @SuppressWarnings("rawtypes")
    public JList getUSERS_List() {
        return USERS_List;
    }

    public JButton getSEND_Button() {
        return SEND_Button;
    }

    @SuppressWarnings("rawtypes")
    public DefaultListModel getListModel() {
        return listModel;
    }

    public JButton getLOGIN_Button() {
        return LOGIN_Button;
    }

    public JButton getLOGOUT_Button() {
        return LOGOUT_Button;
    }

    public JLabel getTEXT_Label() {
        return TEXT_Label;
    }

    public JButton getBRD_Button() {
        return BRD_Button;
    }

    public JTextField getMSG_Text() {
        return MSG_Text;
    }

    @Override
    public String getName() {
        return name;
    }
}
