package cz.cuni.mff.java.chatwu.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient extends Remote {
    // server sends message to client via this method, gives also username of sender
    void retrieveMessage(String from, String message) throws RemoteException;

    // server sends registration information of new user to client via this method, gives also username of source
    void newUserOnline(String from) throws RemoteException;
}