package ds.assign.tom;

import java.io.IOException;
import java.util.Collections; 
import java.io.BufferedWriter;
import java.io.FileWriter;

public class PeerClientDeliver implements Runnable  {
  @Override
  public void run()   {
    try {
      // Save the output to the file L{Peer.identifier}.txt
      BufferedWriter writer = new BufferedWriter(new FileWriter("output/" + "L" + Peer.getIdentifier() + ".txt"));
      while (true) {
        //  Wait until being notified
        Peer.lockQueue();
        while (Peer.getQueueSize() == 0) Peer.waitSignalQueue();
        Peer.unlockQueue(); 
        if (Peer.getMinimum() < 1) continue; 
        Message msg = Peer.getMessageQueue();
        assert(msg != null);
        if (!msg.getWord().equals("ack")) {
            writer.write(msg.getWord() + '\n');
            writer.flush();
            System.out.println("--> " + msg.getWord());
        }
        Peer.decrementKey(msg.getIdentifier());
      }
    } catch (IOException io) {
      System.out.println("Couldn't write to file");
    }
  }
}
