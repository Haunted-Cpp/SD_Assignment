package ds.assign.trg;

import java.util.concurrent.TimeUnit;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.grpc.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;


public class PeerClient implements Runnable {
  
  public static ConcurrentLinkedQueue<Operation> queue = new ConcurrentLinkedQueue<>();
  
  private final StartServiceGrpc.StartServiceBlockingStub blockingStub;
  private final StartRequest request;
  private final ManagedChannel channel;
  
  private final String forwardingHost;
  private final String calculatorHost;
  private final int port;
  
  public PeerClient(int port, String forwardingHost, String calculatorHost) {
    
    this.forwardingHost = forwardingHost;    
    this.calculatorHost = calculatorHost;    
    this.port = port;
    
    // Start the thread that creates the requests
    new Thread(new CreateRequests()).start();
    
    // setup communication channels
    this.channel = ManagedChannelBuilder.forAddress(forwardingHost, port).usePlaintext().build();
    this.request = StartRequest.newBuilder().setStart(true).build();
    this.blockingStub = StartServiceGrpc.newBlockingStub(channel);
  }
  
  @Override
  public void run() {
    
    while (true) {
      
      Peer.lock();
      while (Peer.getToken() == 0) Peer.waitSignal();
      Peer.useToken();
      Peer.unlock();
      
      System.out.println("-----  Token received  -----");
      CalculatorClient calculator = new CalculatorClient(calculatorHost, port);
      int requests = PeerClient.queue.size();
      while (!PeerClient.queue.isEmpty()) {
        Operation task = queue.poll();
        System.out.println(String.format("%+.4f", calculator.compute(task.operation, task.firstValue, task.secondValue)));
      }
      calculator.shutdownChannel();
      System.out.println(String.format("---> %3d requests done -----\n", requests));
      try {
        Thread.sleep(750);
        MessageRequest reply = blockingStub.sendStartToken(request);
      } catch (Exception e1) {
        System.out.println("Receiver seems to be offline ... shutting down!");
        try {
          channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS); 
        } catch (Exception e2) {
          System.out.println("Channel could not be shut down");
        }
        System.exit(0);
      }
        
    }
  }
}
