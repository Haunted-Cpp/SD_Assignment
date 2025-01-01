package ds.assign.tom;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.List;
import java.util.ArrayList;

public class Broadcast implements Runnable {
  private static List<MessageGrpc.MessageBlockingStub> stubs;
  private static List<String> peer_list;
  public Broadcast(List<String> peer_list, int port) {
    this.peer_list = peer_list;
    this.stubs = new ArrayList<>();
    for (int i = 0; i < peer_list.size(); i++) {
      ManagedChannel channel = ManagedChannelBuilder.forAddress(peer_list.get(i), port).usePlaintext().build();
      MessageGrpc.MessageBlockingStub blockingStub = MessageGrpc.newBlockingStub(channel);
      stubs.add(blockingStub);
    }
  }
  @Override
  public void run() {
    while (true) {
      //  Wait until being notified
      Peer.lockBroadcastQueue();
      while (Peer.getBroadcastQueueSize() == 0) Peer.waitSignalBroadcastQueue();
      Peer.unlockBroadcastQueue(); 
      while (Peer.getBroadcastQueueSize() > 0) {
        Message msg = Peer.getMessageBroadcastQueue();
        assert (msg != null);
        for (int i = 0; i < peer_list.size(); i++) {
          try {
            MessageRequest request = MessageRequest.newBuilder().setWord(msg.getWord()).setTimestamp(msg.getTimestamp()).setIdentifier(msg.getIdentifier()).build();
            stubs.get(i).sendMsg(request);
          } catch (java.lang.Exception e) {
            System.out.println("One of the hosts seems to be offline!");
            System.exit(0);
          }
        }
      }
    }
  }
}
