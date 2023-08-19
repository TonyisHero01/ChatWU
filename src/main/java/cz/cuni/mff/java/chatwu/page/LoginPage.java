package cz.cuni.mff.java.chatwu.page;

import cz.cuni.mff.java.chatwu.dto.UserDto;
import cz.cuni.mff.java.chatwu.impl.ChatClientImpl;
import cz.cuni.mff.java.chatwu.interfaces.ChatClient;
import cz.cuni.mff.java.chatwu.interfaces.ChatServer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginPage implements ChangeListener {
    JFrame frame = new JFrame("Login Page");
    JPanel panel = new JPanel();
    JLabel a = new JLabel("username");
    JTextField userText = new JTextField(15);
    JLabel b = new JLabel("password");
    JPasswordField passwordText = new JPasswordField(15);
    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");

    JLabel ipLabel = new JLabel("IP");
    JTextField ipText = new JTextField("127.0.0.1",15);

    JLabel portLabel =  new JLabel("Port Number");
    JTextField portText = new JTextField("1099",15);


    public LoginPage() {
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#EDF0F5")); // set background color

        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(15, 15, 15, 15);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.decode("#FFFFFF")); // set panel background color

        panel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5,
                Color.decode("#2E8B57")));

        loginButton.setBackground(Color.decode("#2E8B57"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Cambria", Font.BOLD, 20));
        loginButton.setPreferredSize(new Dimension(150, 40));

        registerButton.setBackground(Color.decode("#2E8B57"));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Cambria", Font.BOLD, 20));
        registerButton.setPreferredSize(new Dimension(150, 40));

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(a, constraints);

        constraints.gridx = 1;
        panel.add(userText, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(b, constraints);

        constraints.gridx = 1;
        panel.add(passwordText, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(ipLabel, constraints);
        constraints.gridx = 1;
        panel.add(ipText, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(portLabel, constraints);
        constraints.gridx = 1;
        panel.add(portText, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(loginButton, constraints);

        constraints.gridx = 1;
        panel.add(registerButton, constraints);
        frame.add(panel);

        ActionListener loginButtonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();  // get entered username
                String password = String.valueOf(passwordText.getPassword());  // get entered password
                UserDto userDto = new UserDto(username,password);

                //panel.repaint();

                try {
                    // connect server, get server object
                    ChatServer server = connectServer();
                    // if password didn't match
                    if(!server.checkPassword(userDto)){
                        JOptionPane.showMessageDialog(null,
                                "username or password is not correct");
                        return;
                    }

                    // client object register to server
                    ChatClient client = new ChatClientImpl(username);
                    server.registerClient(username,client);
                    // if login successful, redirects to chat page and send
                    new ChatPage((ChatClientImpl) client,server);
                    frame.dispose(); // close login page
                }catch (Exception exception){
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "connecting chat server failed, please check IP address and port number");
                }
            }
        };
        loginButton.addActionListener(loginButtonActionListener);

        loginButton.registerKeyboardAction(loginButtonActionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // add action listener for register button
        ActionListener registerButtonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // connect server
                    ChatServer server = connectServer();
                    UserDto userDto = new UserDto(userText.getText(),new String(passwordText.getPassword()));
                    // invoke registering method of server
                    server.addUser(userDto);
                    JOptionPane.showMessageDialog(null, "Register successful");
                }catch (Exception exception){
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Register failed");
                }
            }
        };
        registerButton.addActionListener(registerButtonActionListener);
        frame.setVisible(true);
    }


    /**
     * server connection
     * @return
     */
    private ChatServer connectServer() throws Exception {
        // connect to RMI registry of local port 1099
        Registry registry = LocateRegistry.getRegistry(ipText.getText(),
                Integer.parseInt(portText.getText()));

        // find server object in registry
        ChatServer server = (ChatServer) registry.lookup("ChatServer");
        return server;
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}