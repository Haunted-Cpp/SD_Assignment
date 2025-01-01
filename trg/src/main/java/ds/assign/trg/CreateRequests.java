package ds.assign.trg;

import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

enum OperationType {
  
  ADD, SUB, MULT, DIV;
  
  private static final List<OperationType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();

  public static OperationType randomOperation()  {
    return VALUES.get(CreateRequests.RANDOM.nextInt(SIZE));
  }
  
};

class Operation {
  
  private static final int LOWER_BOUND = -10;
  private static final int UPPER_BOUND = 10;
  
  OperationType operation;
  double firstValue;
  double secondValue;
  
  Operation() {
    this.firstValue = LOWER_BOUND + CreateRequests.RANDOM.nextDouble() * (UPPER_BOUND - LOWER_BOUND);
    this.secondValue = LOWER_BOUND + CreateRequests.RANDOM.nextDouble() * (UPPER_BOUND - LOWER_BOUND);
    this.operation = OperationType.randomOperation();
    
    // Avoid generating divisions by zero
    while (this.operation == OperationType.DIV && this.secondValue == 0) {
      this.secondValue = LOWER_BOUND + CreateRequests.RANDOM.nextDouble() * (UPPER_BOUND - LOWER_BOUND);
    }
  }
}

public class CreateRequests implements Runnable {
  
  public static final Random RANDOM = new Random(System.nanoTime());
  
  private static final double LAMBDA = 4; // 4 requests per minute (on average)
  private static final PoissonProcess PP = new PoissonProcess(LAMBDA, new Random(System.nanoTime()));
  
  @Override
  public void run() {
    while (true) {
      try {
        double time_for_next_event = PP.timeForNextEvent();
        Thread.sleep((long) (time_for_next_event * 1000 * 60));
        PeerClient.queue.add( new Operation() );
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
