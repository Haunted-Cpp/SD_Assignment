package ds.assign.trg;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;  
import java.net.UnknownHostException;  

public class CalculatorServer {
  
  private static Server server;
  private static int request = 0;
  private static String hostname;
  private static int port;

  private void start() throws IOException {
    /* The port on which the server should run */
    server = ServerBuilder.forPort(port)
        .addService(new CalculatorImpl())
        .addService(new End()) 
        .build()
        .start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          CalculatorServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      System.out.println("Running Calculator Server at " + hostname + " @ " + port);
      System.out.println("-----------------------------------------\n");
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    
    // Make sure the port is given
    if (args.length != 1) {
      System.out.println("*** Usage: `port` ***");
      return;
    }
    
    try {  
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException ex) {  
      ex.printStackTrace();  
    } 
    
    try {
      // Convert the port to an integer
      port = Integer.parseInt(args[0]);
    } catch (java.lang.Exception e) {
      System.out.println("*** Error while converting the given arguments to integers ***");
      throw new RuntimeException(e);
    }
    
    final CalculatorServer server = new CalculatorServer();
    server.start();
    server.blockUntilShutdown();
  }
  
  static class End extends EndServiceGrpc.EndServiceImplBase {
    @Override
    public void sendEndToken(StartRequest can_end, io.grpc.stub.StreamObserver<MessageRequest> replyObserver) {
      try {
        System.out.println("\nReceived end signal!");
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

  static class CalculatorImpl extends CalculatorGrpc.CalculatorImplBase {
    @Override
    public void add(CalculatorRequest req, StreamObserver<CalculatorReply> replyObserver) {
      System.out.println(String.format("#%4d: %+.4f + %+.4f = %+.4f", ++request, req.getValue1(), req.getValue2(), req.getValue1() + req.getValue2()));
      CalculatorReply reply = CalculatorReply.newBuilder().setValue(req.getValue1() + req.getValue2()).build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
    }
    @Override
    public void sub(CalculatorRequest req, StreamObserver<CalculatorReply> replyObserver) {
      System.out.println(String.format("#%4d: %+.4f - %+.4f = %+.4f", ++request, req.getValue1(), req.getValue2(), req.getValue1() - req.getValue2()));
      CalculatorReply reply = CalculatorReply.newBuilder().setValue(req.getValue1() - req.getValue2()).build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
    }
    @Override
    public void mul(CalculatorRequest req, StreamObserver<CalculatorReply> replyObserver) {
      System.out.println(String.format("#%4d: %+.4f * %+.4f = %+.4f", ++request, req.getValue1(), req.getValue2(), req.getValue1() * req.getValue2()));
      CalculatorReply reply = CalculatorReply.newBuilder().setValue(req.getValue1() * req.getValue2()).build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
    }
    @Override
    public void div(CalculatorRequest req, StreamObserver<CalculatorReply> replyObserver) {
      System.out.println(String.format("#%4d: %+.4f / %+.4f = %+.4f", ++request, req.getValue1(), req.getValue2(), req.getValue1() / req.getValue2()));
      CalculatorReply reply = CalculatorReply.newBuilder().setValue(req.getValue1() / req.getValue2()).build();
      replyObserver.onNext(reply);
      replyObserver.onCompleted();
    }
  }
}
