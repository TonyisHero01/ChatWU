package cz.cuni.mff.java.chatwu.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 消息监听，当客户端收到消息，页面可以监听到并改变页面中的元素，需要页面实现此监听器
 */
public interface ClientMessageEventListener extends Remote {
    // client notify page that received message
    void onMessageReceived(String from,String message) throws RemoteException;


    // 客户端通知页面，有新用户注册
    // client notify page that new user registered
    void onNewUserOnline(String from) throws RemoteException;

}