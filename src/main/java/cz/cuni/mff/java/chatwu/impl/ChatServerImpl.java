package cz.cuni.mff.java.chatwu.impl;

import cz.cuni.mff.java.chatwu.dao.DatabaseService;
import cz.cuni.mff.java.chatwu.dto.UserDto;
import cz.cuni.mff.java.chatwu.interfaces.ChatClient;
import cz.cuni.mff.java.chatwu.interfaces.ChatServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer, Serializable {

    private static final long serialVersionUID = 1L;
    // save all registered users, where keys are client names
    private Map<String, ChatClient> clients = new HashMap<String, ChatClient>();

    public ChatServerImpl() throws RemoteException, SQLException {
        DatabaseService databaseService = new DatabaseService();
        databaseService.createTable();
    }

    // client invoke this method to register himself to server
    @Override
    public void registerClient(String name,ChatClient client) throws RemoteException {
        this.clients.put(name, client);
    }

    // server broadcast message to all users via this method
    @Override
    public void broadcastMessage(String from,String message) throws RemoteException {
        for (String name : clients.keySet()) {
            // skip client himself
            if(name.equals(from)){
                continue;
            }
            clients.get(name).retrieveMessage("Public Channel", from+":"+message);
        }
    }

    // server sends private message to the given client via this method
    @Override
    public void sendPrivateMessage(String from,String to, String message) throws RemoteException {
        ChatClient client = clients.get(to);
        if (client != null) {
            client.retrieveMessage(from, message);
        }
    }

    // client invoke this server method to register user
    @Override
    public void addUser(UserDto userDto) throws SQLException,RemoteException {
        DatabaseService databaseService = new DatabaseService();
        databaseService.addUser(userDto);
        // after registration, notify all clients to add this user to the user list
        for (String name : clients.keySet()) {
            // skip client himself
            if(name.equals(userDto.getName())){
                continue;
            }
            clients.get(name).newUserOnline(userDto.getName());
        }
    }

    // client invoke this method to inquire user list
    @Override
    public List<UserDto> getUserList() throws SQLException,RemoteException {
        DatabaseService databaseService = new DatabaseService();
        return databaseService.getUserList();
    }

    @Override
    public boolean checkPassword(UserDto userDto) throws SQLException,RemoteException {
        DatabaseService databaseService = new DatabaseService();
        // according to username and password to search user, if user exists, means password is correct
        List<UserDto> list = databaseService.getUser(userDto);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }



}
