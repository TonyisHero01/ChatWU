package cz.cuni.mff.java.chatwu.page;

import cz.cuni.mff.java.chatwu.dto.UserDto;
import cz.cuni.mff.java.chatwu.impl.ChatClientImpl;
import cz.cuni.mff.java.chatwu.interfaces.ChatServer;
import cz.cuni.mff.java.chatwu.interfaces.ClientMessageEventListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatPage implements ClientMessageEventListener, Serializable {
    private static final long serialVersionUID = 1L;
    private ChatClientImpl client;  // client object
    private ChatServer server;  // server object
    JFrame frame = new JFrame();
    DefaultListModel<String> listModel = new DefaultListModel<>();  // save friend list
    JList<String> friendsList = new JList<>(listModel);  // show friend list

    JTextArea chatArea = new JTextArea();
    JTextField messageField = new JTextField(20);
    JButton sendButton = new JButton("Send");

    Map<String, Integer> unreadMessages = new HashMap<>();  // save the number of unread messages

    // create a HashMap to save history with each friend
    HashMap<String, String> chatHistory = new HashMap<String, String>();



    public ChatPage(ChatClientImpl client,ChatServer server) throws Exception {
        this.client = client;
        this.server = server;
        frame.setTitle(client.getClientName());
        // add current page to listener
        client.setClientMessageEventListener(this);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // inquire all users
        List<UserDto> frList = server.getUserList();
        // add public chat room
        listModel.addElement("Public Channel");
        // initialize the number of unread messages
        unreadMessages.put("Public Channel", 0);
        for (UserDto u : frList) {
            // if it is yourself, skip, otherwise add to friend list
            if(client.getClientName().equals(u.getName())){
                continue;
            }
            // add friend to list
            listModel.addElement(u.getName());
            // initialize the number of unread messages
            unreadMessages.put(u.getName(), 0);
        }

        // select the first friend from the friend list
        friendsList.setSelectedIndex(0);

        // show friends name and the numbers of unread messages
        friendsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                int unread = unreadMessages.get(value);
                if (unread > 0) {
                    setText(value + " (" + unread + " unread)");
                }
                return c;
            }
        });

        // add ListSelectionListener to JList, listen click of various friend and change chat area
        friendsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // get selected friend
                String selectedFriend = friendsList.getSelectedValue();
                // set the number of unread messages to 0
                unreadMessages.put(selectedFriend, 0);
                // get history from HashMap
                String history = chatHistory.get(selectedFriend);

                // update chat area
                chatArea.setText(history);
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.setLayoutOrientation(JList.VERTICAL);
        friendsList.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(friendsList);
        listScroller.setPreferredSize(new Dimension(150, 80));
        mainPanel.add(listScroller);

        // configuration chat area on the right side
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);  // automatic line break
        JScrollPane chatScroller = new JScrollPane(chatArea);
        chatScroller.setPreferredSize(new Dimension(400, 300));
        rightPanel.add(chatScroller);

        // configuration of input and send button area
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));

        messageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, messageField.getPreferredSize().height));
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        rightPanel.add(inputPanel);
        mainPanel.add(rightPanel);

        frame.getContentPane().add(mainPanel);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#EDF0F5"));

        sendButton.setBackground(Color.decode("#2E8B57"));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Cambria", Font.BOLD, 20));

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();  // get message from input box
                if (!message.isEmpty()) {  // if message is not null
                    // get current chat history
                    String current = chatHistory.get(friendsList.getSelectedValue());
                    // update history
                    chatHistory.put(friendsList.getSelectedValue(),
                            (current==null?"":current+"\n")+client.getClientName()+": " + message);
                    messageField.setText("");  // Clear the input box
                    // update chat area
                    chatArea.setText(chatHistory.get(friendsList.getSelectedValue()));
                    // send to server
                    try {
                        if("Public Channel".equals(friendsList.getSelectedValue())){
                            server.broadcastMessage(client.getClientName(),message);
                        }else {
                            // private message
                            server.sendPrivateMessage(client.getClientName(),
                                    friendsList.getSelectedValue(),message);
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Sending Message Failed");
                    }
                }
            }
        };

        sendButton.addActionListener(actionListener);
        sendButton.registerKeyboardAction(actionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    // invoke this method when listened new message
    @Override
    public void onMessageReceived(String from,String message) {
        // update chat history
        String current = chatHistory.get(from) == null ? "" : chatHistory.get(from) + "\n";
        // private message: history+sender: message
        chatHistory.put(from, current + from + ": " + message);

        // public chat room: history+message
        chatHistory.put("Public Channel", current + message);

        // if this message came from current unselected friend, update the number of unread message and refresh friend list
        if (!from.equals(friendsList.getSelectedValue())) {
            // unread+1
            unreadMessages.put(from, unreadMessages.get(from) + 1);
            // repaint friend list
            friendsList.repaint();
        }else {
            // update chat area
            chatArea.setText(chatHistory.get(from));
        }
    }

    // new user registered, refresh friend list
    @Override
    public void onNewUserOnline(String from){
        // add new friend
        listModel.addElement(from);
        // add the number of unread messages
        unreadMessages.put(from, 0);
        // repaint friend list
        friendsList.repaint();
    }
}