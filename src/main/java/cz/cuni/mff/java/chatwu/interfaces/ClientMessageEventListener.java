package cz.cuni.mff.java.chatwu.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * message listening, when client receive message, page can listen and change elements in page. Need to implement this listener
 */
public interface ClientMessageEventListener extends Remote {
    // client notify page that received message
    void onMessageReceived(String from,String message) throws RemoteException;

    // client notify page that new user registered
    void onNewUserOnline(String from) throws RemoteException;

}