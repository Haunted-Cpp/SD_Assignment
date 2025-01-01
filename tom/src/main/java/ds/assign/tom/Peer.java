package ds.assign.tom;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.Random;
import java.net.InetAddress;  
import java.net.UnknownHostException;  

public class Peer {
  
  private  static List<String>  word_list = new ArrayList<>();
  private  static List<String>  peer_list = new ArrayList<>();
  
  private  static PriorityBlockingQueue<Message> pq = new PriorityBlockingQueue<>();
  private  static Queue<Message> broadcast_queue = new ConcurrentLinkedQueue<>(); 
  
  private  static int lamportClock = 0;
  private  static Map<Integer, Integer> queueStatus = new ConcurrentSkipListMap<>();  
  
  private  static final ReentrantLock lock = new ReentrantLock();
  
  private  static int identifier;
  private  static int port;
  private  static String hostname;

  /**
    * Main launches the server from the command line.
  */
  public static void main(String[] args) {
    
    // Make sure two arguments are given
    if (args.length < 2) {
      System.out.println("*** Usage: `port` `network` ***");
      return;
    }

    port  = Integer.parseInt(args[0]);
    
    try {  
      hostname = InetAddress.getLocalHost().getHostName();
      identifier = Integer.parseInt( hostname.substring(1) );
    } catch (UnknownHostException ex) {  
      ex.printStackTrace();  
    } 
    
    
    for (int i = 1; i < args.length; i++) peer_list.add(args[i]);
        
    try {
      Scanner scanner = new Scanner(new File("dict.txt"));
      while (scanner.hasNextLine()) word_list.add(scanner.nextLine());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    // Start the thread that runs the server
    new Thread(new PeerServer(peer_list, port)).start();     // This will deal with TOMreceive(m) 
    new Thread(new PeerClientDeliver()).start();  // This will deal with Tomdeliver()
    new Thread(new PeerClientSend()).start(); // This will deal with TOMsend()
  }
  
  public static String getHostname() {
    return hostname;
  }
  
  public static int getIdentifier() {
    return identifier;
  }
  
  public static void lock() {
    lock.lock();
  }
  
  public static void unlock() {
    lock.unlock();
    
  }
  
  public static int getLamport() {
    return lamportClock;
  }
  
  public static int incrementAndGet() {
    lamportClock++;
    return lamportClock;
  }
  
  public static int setAndGet(int value) {
    lamportClock = value;
    return lamportClock;
  }
  
  public static String getRandomWord(Random rng) {
    return word_list.get(rng.nextInt(word_list.size()));
  }
  
  private static ReentrantLock lockBroadcastQueue = new ReentrantLock();
  private static Condition conditionBroadcastQueue = lockBroadcastQueue.newCondition();
  
  public static void lockBroadcastQueue() {
    lockBroadcastQueue.lock();
  }
  
  public static void unlockBroadcastQueue() {
    lockBroadcastQueue.unlock();
  }
  
  public static void waitSignalBroadcastQueue() {
    try {
      conditionBroadcastQueue.await();
    } catch (InterruptedException e) {
      // exception
      System.out.println("Shutting down due to exception");
      System.exit(0);
    }
  }
  
  public static void appendMessageBroadcastQueue(Message msg) {
    lockBroadcastQueue();
    broadcast_queue.add(msg);
    
    int lst = -1;
    for (Message m : broadcast_queue) {
      assert(lst < m.getTimestamp());
      lst = m.getTimestamp();
    }
    
    conditionBroadcastQueue.signal();
    unlockBroadcastQueue(); 
  }

  private static ReentrantLock lockQueue = new ReentrantLock();
  private static Condition conditionQueue = lockQueue.newCondition();
  
  public static void lockQueue() {
    lockQueue.lock();
  }
  
  public static void unlockQueue() {
    lockQueue.unlock();
  }
  
  public static void waitSignalQueue() {
    try {
      conditionQueue.await();
    } catch (InterruptedException e) {
      // exception
      System.out.println("Shutting down due to exception");
      System.exit(0);
    }
  }
  
  public static void appendMessageQueue(Message msg) {
    lockQueue();
    pq.add(msg);
    conditionQueue.signal();
    unlockQueue(); 
  }
  
  public static int getBroadcastQueueSize() {
    return broadcast_queue.size();
  }
  
  public static int getQueueSize() {
    return pq.size();
  }
  
  public static int getMinimum() {
    if (queueStatus.size() < peer_list.size()) return 0;
    assert(queueStatus.size() == peer_list.size());
    return Collections.min( queueStatus.values() );
  }
  
  public static void decrementKey(int key) {
    queueStatus.compute(key, (keyOther, value) -> value - 1);
  }
  
  public static void incrementKey(int key) {
    queueStatus.computeIfAbsent (key, keyOther -> Integer.valueOf(0));
    queueStatus.computeIfPresent(key, (keyOther, value) -> value + 1);
  }
  
  public static Message getMessageQueue() {
    assert(pq.size() > 0);
    return pq.poll();
  }
  
  public static Message getMessageBroadcastQueue() {
    assert(broadcast_queue.size() > 0);
    return broadcast_queue.poll();
  }

}



