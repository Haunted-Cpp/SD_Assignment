package ds.assign.tom;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class PeerServer implements Runnable {
  
  private static Server server;
  private static int port;
  private static List<String> peer_list;
  
  public PeerServer(List<String> peer_list, int port) {
    this.port = port;    
    this.peer_list = peer_list;
  }
  
  @Override
  public void run() {
    /* The port on which the server should run */
    try {
      server = ServerBuilder.forPort(port)
      .addService(new MessageImpl())
      .addService(new Start())  
      .addService(new End())  
      .build().start();
      if (server != null) {
        System.out.println("Running at " + Peer.getHostname() + " @ " + port);
        System.out.println("----------------------------");
        System.out.println("    Waiting for token...    ");
        System.out.println("----------------------------");
        System.out.println("");
        server.awaitTermination();
      }
    } catch (java.lang.Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  static class Start extends StartServiceGrpc.StartServiceImplBase {
    @Override
    public void sendStartToken(StartRequest can_start, io.grpc.stub.StreamObserver<MessageRequest> replyObserver) {
      try {
        System.out.println("Received start signal!\n");
        new Thread(new Broadcast(peer_list, port)).start(); 
        MessageRequest reply = MessageRequest.newBuilder().setWord("OK!").build();
        replyObserver.onNext(reply);
        replyObserver.onCompleted();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  static class End extends EndServiceGrpc.EndServiceImplBase {
    @Override
    public void sendEndToken(StartRequest can_end, io.grpc.stub.StreamObserver<MessageRequest> replyObserver) {
      try {
        System.out.println("Received end signal!");
        MessageRequest reply = MessageRequest.newBuilder().setWord("OK!").build();
        replyObserver.onNext(reply);
        replyObserver.onCompleted();
        if (server != null) server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        System.exit(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  static class MessageImpl extends MessageGrpc.MessageImplBase {
    public void sendMsg(MessageRequest req, StreamObserver<MessageRequest> replyObserver) {
      Message msg = new Message(req.getWord(), req.getTimestamp(), req.getIdentifier());
      int ts = msg.getTimestamp();
      Peer.lock();
      int cj = Peer.setAndGet( Math.max(Peer.getLamport(), ts) + 1);
      if (!msg.getWord().equals("ack")) {
        Message ack = new Message("ack", cj, Peer.getIdentifier());
        Peer.appendMessageBroadcastQueue(ack);
      }
      Peer.appendMessageQueue(msg);
      Peer.incrementKey(msg.getIdentifier());
      Peer.unlock();
      MessageRequest reply = MessageRequest.newBuilder().setWord("OK!").build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
    }
  }
}
