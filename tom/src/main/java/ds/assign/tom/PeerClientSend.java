package ds.assign.tom;

import java.util.Random;

public class PeerClientSend implements Runnable {
  private static final Random RANDOM = new Random(System.nanoTime());
  private static final double LAMBDA = 60; // frequency of 60 per minute (1 per second)
  private static final PoissonProcess PP = new PoissonProcess(LAMBDA, new Random(System.nanoTime()));
  @Override
  public void run() {
    while (true) {
      try {
        double time_for_next_event = PP.timeForNextEvent();
        Thread.sleep((long) (time_for_next_event * 60 * 1000));
        
        Peer.lock();
        int ci = Peer.incrementAndGet();
        Message msg = new Message(Peer.getHostname() + Peer.getRandomWord(RANDOM), ci, Peer.getIdentifier());
        Peer.appendMessageBroadcastQueue(msg);
        Peer.unlock();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
