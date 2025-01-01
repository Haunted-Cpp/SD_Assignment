package ds.assign.p2p;

import java.net.InetAddress;  
import java.net.UnknownHostException;  
import java.util.List;  
import java.util.ArrayList;  

public class Peer {
  private static String hostname;
  public static List<String> network = new ArrayList<>();
  public static void main(String[] args) {
    // Make sure two arguments are given
    if (args.length < 3) { 
      System.out.println("*** Usage: `timeout` `port` `known hosts` ***");
      return;
    }
    try {  
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {  
      e.printStackTrace();  
      return;
    } 
    int port;
    int timeout;
    // Convert the arguments to integers
    try{
      timeout = Integer.parseInt(args[0]);
      port  = Integer.parseInt(args[1]);
      for (int i = 2; i < args.length; i++) network.add(args[i]);
    } catch (java.lang.Exception e) {
      System.out.println("*** Error while converting the given arguments to integers ***");
      throw new RuntimeException(e);
    }
    // Start the thread that runs the server
    new Thread(new PeerServer(port, timeout)).start();
  }
  
  public static String getHostname() {
    return hostname;
  }
}
