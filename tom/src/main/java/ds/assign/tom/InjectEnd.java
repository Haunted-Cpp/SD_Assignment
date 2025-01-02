package ds.assign.tom;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.net.InetAddress;  
import java.net.UnknownHostException;

public class InjectEnd {

  public static void end(String host, int port) {
    try {
      InetAddress address = InetAddress.getByName(host); 
    } catch (UnknownHostException e) {
      System.out.println("Resolution of hostname \"" + host + "\" failed");
      return;
    }
    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    EndServiceGrpc.EndServiceBlockingStub blockingStub = EndServiceGrpc.newBlockingStub(channel);
    StartRequest request = StartRequest.newBuilder().setStart(true).build();
    try {
      blockingStub.sendEndToken(request);
      System.out.println("Host " + host + " @ " + port + " is now offline");
    } catch (Exception e) {
      System.out.println("Host " + host + " @ " + port + " seems to be offline");
    }
    channel.shutdown();
  }

  public static void main(String[] args) {
    // Make sure two arguments are given
    if (args.length < 2) {
      System.out.println("*** Usage: `port` `network` ***");
      return;
    }
    int port = Integer.parseInt(args[0]);
    for (int i = 1; i < args.length; i++) end(args[i], port);
  }
}
