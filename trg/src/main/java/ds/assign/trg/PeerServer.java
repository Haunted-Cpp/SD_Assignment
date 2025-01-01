package ds.assign.trg;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

public class PeerServer implements Runnable {
  
  private static Server server;
  int port;
  
  public PeerServer(int port) {
    this.port = port;    
  }
  
  @Override
  public void run() {
    /* The port on which the server should run */
    try {
      server = ServerBuilder.forPort(port)
      .addService(new Start()) // To receive start signal
      .addService(new End())   // To receive end signal
      .build()
      .start();
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
  
  static class End extends EndServiceGrpc.EndServiceImplBase {
    @Override
    public void sendEndToken(StartRequest can_end, io.grpc.stub.StreamObserver<MessageRequest> replyObserver) {
      try {
        System.out.println("Received end signal!");
        MessageRequest reply = MessageRequest.newBuilder().setMsg("OK!").build();
        replyObserver.onNext(reply);
        replyObserver.onCompleted();
        if (server != null) server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        System.exit(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  static class Start extends StartServiceGrpc.StartServiceImplBase{
    @Override
    public void sendStartToken(StartRequest req, StreamObserver<MessageRequest> replyObserver) {
      // The token is now with the server

      // Send an ACK - The token has been received
      MessageRequest reply = MessageRequest.newBuilder().setMsg("ACK").build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();

      // Notify the thread running "PeerClient" that the Token has been received
      Peer.lock(); 
      Peer.receiveToken();
      Peer.signal();
      Peer.unlock(); 
    }
  }
}
