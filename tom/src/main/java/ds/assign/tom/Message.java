package ds.assign.tom;

public class Message implements Comparable<Message> {
  private String word;
  private int timestamp;
  private int identifier;
  Message(String word, int timestamp, int identifier) {
    this.word = word;
    this.timestamp = timestamp;
    this.identifier = identifier;
  }
  @Override
  public int compareTo(Message anotherMessage) {
    if (this.timestamp != anotherMessage.timestamp) {
      return this.timestamp - anotherMessage.timestamp;
    }
    return this.identifier - anotherMessage.identifier;
  }
  public String getWord() {
    return word;
  }
  public int getTimestamp() {
    return timestamp;
  }
  public int getIdentifier() {
    return identifier;
  }
}
