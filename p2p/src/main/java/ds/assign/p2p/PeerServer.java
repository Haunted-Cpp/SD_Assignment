package ds.assign.p2p;

// import ds.assign.p2p.PeerClient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class PeerServer implements Runnable {
  private static Server server;
  private static int port;
  private static int delta;
  
  public PeerServer(int port, int delta) {
    this.delta = delta;
    this.port = port;
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
        // Start the thread that runs the client
        new Thread(new PeerClient(port, delta)).start();
        MessageRequest reply = MessageRequest.newBuilder().build();
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
        MessageRequest reply = MessageRequest.newBuilder().build();
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
    public void sendToken(MessageRequest req, StreamObserver<MessageRequest> replyObserver) {
      PeerClient.add_peers(req.getValuesList(), req.getTimestampsList());
      // get all the keys and values
      Pair keys_and_values = PeerClient.get_network_info();
      List<String> keys = keys_and_values.getKeys();
      List<Long> values = keys_and_values.getValues();
      // Send to the server the merged result -> Send Port + Server
      MessageRequest reply = MessageRequest.newBuilder().addAllValues(keys).addAllTimestamps(values).build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
      // Show the current status of the network
      PeerClient.printNetwork();
    }
  }
}
