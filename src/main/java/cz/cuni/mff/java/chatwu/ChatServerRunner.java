package cz.cuni.mff.java.chatwu;

import cz.cuni.mff.java.chatwu.impl.ChatServerImpl;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServerRunner {

    public static void main(String[] args) throws RemoteException, UnknownHostException, SQLException, SocketException {
        int port = 1099;
        // create and run registry, running in locals 1099 port
        Registry registry = LocateRegistry.createRegistry(port);

        registry.rebind("ChatServer", new ChatServerImpl());

        //get IP addresses
        System.out.println("Server started, server name is ChatServer");
        System.out.println("Please use client to connect "+getIPv4Address(InetAddress.getLocalHost())+":"+port);
        System.out.println("If the above IP address is not functional, please try IP addresses below: ");
        getOtherIPv4Address();
        while (true){

        }
    }

    /**
     * get IPv4 address
     * @param inetAddress
     * @return
     */
    private static String getIPv4Address(InetAddress inetAddress) {
        if (inetAddress != null) {
            byte[] address = inetAddress.getAddress();
            if (address.length == 4) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.length; i++) {
                    sb.append(address[i] & 0xFF);
                    if (i < address.length - 1) {
                        sb.append(".");
                    }
                }
                return sb.toString();
            }
        }
        return null;
    }
    private static void getOtherIPv4Address() throws SocketException, UnknownHostException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        List<String> IPAddresses = new ArrayList<>();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();

            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                IPAddresses.add(i.getHostAddress());
            }
        }
        // main part of this function is from: https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java

        String strPattern = "\\d*\\.\\d*\\.\\d*\\.\\d*";

        Pattern pattern = Pattern.compile(strPattern);

        for (String IPAddress : IPAddresses) {
            Matcher matcher = pattern.matcher(IPAddress);
            if (matcher.find() && !IPAddress.equals(getIPv4Address(InetAddress.getLocalHost()))) {
                System.out.println(IPAddress);
            }
        }
    }
}