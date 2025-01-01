package ds.assign.trg;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

  private ManagedChannel channel = null; 
  private CalculatorGrpc.CalculatorBlockingStub blockingStub = null;

  public CalculatorClient(String hostname, int port) {
    this.channel = ManagedChannelBuilder.forAddress(hostname, port).usePlaintext().build();
    this.blockingStub = CalculatorGrpc.newBlockingStub(channel);
  }
  
  public void shutdownChannel() {
    try {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    } catch (java.lang.Exception e) {
      System.out.println("Error while shutting down Calculator Client");
      System.exit(0);
    } 
  }

  public double compute(OperationType op, double firstValue, double secondValue) {
    CalculatorRequest request = CalculatorRequest.newBuilder().setValue1(firstValue).setValue2(secondValue).build();
    CalculatorReply reply = null; 
    try {
      switch (op) {
        case ADD:
          System.out.print(String.format("%+.4f + %+.4f = ", firstValue, secondValue));
          reply = blockingStub.add(request);
          break;
        case SUB:
          System.out.print(String.format("%+.4f - %+.4f = ", firstValue, secondValue));
          reply = blockingStub.sub(request);
          break;
        case MULT:
          System.out.print(String.format("%+.4f * %+.4f = ", firstValue, secondValue));
          reply = blockingStub.mul(request);
          break;
        case DIV:
          System.out.print(String.format("%+.4f / %+.4f = ", firstValue, secondValue));
          reply = blockingStub.div(request);
          break;
      }
    } catch (java.lang.Exception e) {
      System.out.println("?\n\nCommunication with Calculator Server failed! Server is likely offline ... shutting down");
      System.exit(0);
    }
    return reply.getValue();
  }
  
}
