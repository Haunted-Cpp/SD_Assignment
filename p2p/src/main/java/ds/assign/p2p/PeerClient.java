package ds.assign.p2p;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentSkipListMap;   
import java.util.Random;
import java.util.List;
import java.util.Iterator; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.Calendar; 
import java.util.GregorianCalendar; 

class Pair {         
  private final List<String>  keys;
  private final List<Long> values;
  
  public Pair(List<String> keys, List<Long> values) {         
    this.keys = keys;
    this.values= values;
  }
  
  public List<String> getKeys() {
    return keys;
  }
  
  public List<Long> getValues() {
    return values;
  }
}

public class PeerClient implements Runnable {
  private static ConcurrentSkipListMap<String, Long> known_peers = new ConcurrentSkipListMap<>();  
  
  private static final Random RANDOM = new Random(System.nanoTime());
  private static final double LAMBDA = 6; // 6 requests per minute - on average
  
  private MessageGrpc.MessageBlockingStub blockingStub;
  private MessageRequest request;
  private ManagedChannel channel;
  
  private static final PoissonProcess PP = new PoissonProcess(LAMBDA, new Random(System.nanoTime()) );
  
  private int port;
  private static int timeout;
  
  PeerClient(int port, int timeout) {
    this.port = port;
    this.timeout = timeout;
  }
  
  public static void printNetwork() {
    removeOld();
    System.out.println("Network @ " + Peer.getHostname());
    for (ConcurrentSkipListMap.Entry<String, Long> entry : known_peers.entrySet()) {
        String k = entry.getKey();
        Long v = entry.getValue();
        Date event = new Date(v);
        Calendar calendar = GregorianCalendar.getInstance();  
        calendar.setTime(event);   
        System.out.printf("%s --> %02d:%02d:%02d\n", k, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));
    }
    System.out.println("");
  }
  
  public static void updateKey(String key, Long value) {
    Long old_value = known_peers.putIfAbsent(key, value);
    if (old_value != null) { // key already exists -> keep the maximum
      known_peers.put(key, Math.max(old_value, value));
    }
  }
  
  private static void removeOld() {
    updateKey(Peer.getHostname(), System.currentTimeMillis()); // update own timestamp
    Long current_time = System.currentTimeMillis();
    known_peers.entrySet().removeIf(entry -> entry.getValue() < current_time - timeout * 1000L);
  }
  
  // get all the keys and values
  public static Pair get_network_info() {
    List<String> keys = new ArrayList<>();
    List<Long> values = new ArrayList<>();
    for(ConcurrentSkipListMap.Entry<String, Long> entry : known_peers.entrySet()) {
      keys.add(entry.getKey());
      values.add(entry.getValue());
    }
    return new Pair(keys, values);
  }
  
  public static void add_peers(List<String> valuesList, List<Long> timestampsList) {
    Iterator<String> portIterator       = valuesList.iterator();
    Iterator<Long> timestampIterator = timestampsList.iterator();
    // Add to set the received port values + update timestamp -> iterate simultaneously
    assert(valuesList.size() == timestampsList.size());
    while (portIterator.hasNext() && timestampIterator.hasNext()) {
      String port        = portIterator.next();
      Long timestamp     = timestampIterator.next();
      updateKey(port, timestamp);
    }
    removeOld();
  }
  
  private String getForwardingHost() {
    double time_for_next_event = PP.timeForNextEvent();
    try {
      Thread.sleep((long) (time_for_next_event * 60 * 1000));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    String forwardingHost = "";
    int size = known_peers.size() - 1; // exclude myself
    if (size == 0) return forwardingHost;
    int position = RANDOM.nextInt(size); 
    int current_position = 0;
    for(String hostname : known_peers.keySet()) {
      if (hostname == Peer.getHostname()) continue; // ignore myself
      if (position == current_position) {
        forwardingHost = hostname;
        break;
      }
      current_position++;
    }
    return forwardingHost;
  }
  
  private void pushAndPull(String hostname) {
    assert(!hostname.equals(""));
    // setup communication channels
    this.channel = ManagedChannelBuilder.forAddress(hostname, port).usePlaintext().build();
    // get all the keys and values
    removeOld();
    Pair keys_and_values = get_network_info();
    List<String> keys = keys_and_values.getKeys();
    List<Long> values = keys_and_values.getValues();
    // prepare channel to send map
    this.request = MessageRequest.newBuilder().addAllValues(keys).addAllTimestamps(values).build();
    this.blockingStub = MessageGrpc.newBlockingStub(channel);
    try {
      MessageRequest reply = blockingStub.sendToken(request);
      add_peers(reply.getValuesList(), reply.getTimestampsList());
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
     } catch (java.lang.Exception e) {
       // the host is offline
    }
  }
  
  @Override
  public void run() {
    for (String hostname : Peer.network) updateKey(hostname, System.currentTimeMillis());
    while (true) {
      printNetwork();
      String forwardingHost = getForwardingHost();
      if (forwardingHost.equals("")) continue;
      pushAndPull(forwardingHost);
    }
  }
}
