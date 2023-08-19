package cz.cuni.mff.java.chatwu.interfaces;

import cz.cuni.mff.java.chatwu.dto.UserDto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

// Define remote interface of server, this interface defines remote methods that client can invoke
public interface ChatServer extends Remote {
    // Client invokes this method to register himself to the server, username is given
    void registerClient(String name,ChatClient client) throws RemoteException;

    // Server broadcasts message to all users via this method
    void broadcastMessage(String from,String message) throws RemoteException;

    // Server sends private message to given client via this method
    void sendPrivateMessage(String from,String to, String message) throws RemoteException;

    // Register user
    void addUser(UserDto userDto) throws SQLException, RemoteException;

    // Inquire user list
    List<UserDto> getUserList() throws SQLException, RemoteException;

    boolean checkPassword(UserDto userDto) throws SQLException, RemoteException;

}