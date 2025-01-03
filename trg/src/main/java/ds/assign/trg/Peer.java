package ds.assign.trg;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.net.InetAddress;  
import java.net.UnknownHostException;  

public class Peer {
 
  private static ReentrantLock lock = new ReentrantLock();
  private static Condition condition = lock.newCondition();
  private static int hasToken = 0;
  private static String hostname;

  /**
    * Main launches the server from the command line.
  */
  public static void main(String[] args) throws IOException, InterruptedException {

    // Make sure three arguments are given
    if (args.length != 3) {
      System.out.println("*** Usage: `port` `forwardingHost` `calculatorHost` ***");
      return;
    }
    

    int port;
    
    // The names of the hosts
    String forwardingHost = args[1];
    String calculatorHost = args[2];

    try{
      // Convert the port to an integer
      port = Integer.parseInt(args[0]);
    } catch (java.lang.Exception e) {
      System.out.println("*** Error while converting the given arguments to integers ***");
      throw new RuntimeException(e);
    }
    
    try {  
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException ex) {  
      ex.printStackTrace();  
    } 

    // Start the thread that runs the server
    new Thread(new PeerServer(port)).start();
    // Start the thread that runs the client
    new Thread(new PeerClient(port, forwardingHost, calculatorHost)).start();
  }
  
  public static int getToken() {
    return hasToken;
  }
  public static void useToken() {
    assert(hasToken == 1);
    hasToken = 0;
  }
  public static void receiveToken() {
    hasToken = 1;
  }
  
  public static void lock() {
    lock.lock();
  }
  
  public static void unlock() {
    lock.unlock();
  }
  
  public static void waitSignal() {
    try {
      if (!Peer.condition.await(60, TimeUnit.SECONDS)) {
        System.out.println("Shutting down due to inactivity");
        System.exit(0);
      }
    } catch (InterruptedException e) {
      // exception
      System.out.println("Shutting down due to exception");
      System.exit(0);
    }
  }
  
  public static void signal() {
    condition.signal();
  }
  
  public static String getHostname() {
    return hostname;
  }
}
