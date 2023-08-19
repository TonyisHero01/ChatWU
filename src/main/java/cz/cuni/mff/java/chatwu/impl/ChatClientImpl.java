package cz.cuni.mff.java.chatwu.impl;

import cz.cuni.mff.java.chatwu.interfaces.ChatClient;
import cz.cuni.mff.java.chatwu.interfaces.ClientMessageEventListener;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClientImpl  extends UnicastRemoteObject implements ChatClient {
    // chat page listener, when new message comes, notify this listener
    private ClientMessageEventListener clientMessageEventListener;

    /**
     * client name
     */
    private String clientName;

    public ChatClientImpl(String clientName) throws RemoteException {
        this.clientName = clientName;
    }

    // server sends message to client via this method, gives also sender name
    @Override
    public void retrieveMessage(String from, String message) throws RemoteException {
        System.out.println(clientName+"发送了");
        //send message to page listener
        clientMessageEventListener.onMessageReceived(from, message);
    }

    // new user registered, notify page to refresh
    @Override
    public void newUserOnline(String from) throws RemoteException {
        clientMessageEventListener.onNewUserOnline(from);
    }

    // page invoke this method to save his listener here
    public void setClientMessageEventListener(ClientMessageEventListener clientMessageEventListener) {
        this.clientMessageEventListener = clientMessageEventListener;

    }

    public ClientMessageEventListener getCilentMessageEventListener() {
        return clientMessageEventListener;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
